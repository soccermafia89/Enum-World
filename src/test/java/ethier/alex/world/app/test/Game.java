/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.world.app.test;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import ethier.alex.world.addon.FilterListBuilder;
import ethier.alex.world.addon.PartitionBuilder;
import ethier.alex.world.core.data.FilterList;
import ethier.alex.world.core.data.Partition;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**

 String schema:
 1=spy, 0=resistance
 2=did not go on mission, 1=spy vote, 0=resistance vote

 For a 3 player game:

 100|103|130|300|300|300 is a game where the first player is a spy and on the first mission they went on with the last player.

 The spy threw a fail. On the second mission the spy went on a mission with the second player and threw a fail.
 On subsequent missions the spy was left off the missions.


 @author alex
 */
public class Game {

    private static Logger logger = LogManager.getLogger(Game.class);
    Collection<FilterList> filters;
    int roundCount = 0;
    private BiMap<String, Integer> players;
    private int[] roundVotes;
    private int[] radices;
    private int spies;

    public Game(List<String> myPlayers, int mySpies, int[] myRoundVotes) {
        roundVotes = myRoundVotes;
        filters = new ArrayList<FilterList>();
        players = HashBiMap.create();
        spies = mySpies;

        logger.info("Player Map");
        for (int i = 0; i < myPlayers.size(); i++) {
            String player = myPlayers.get(i);
            logger.info(player + " <=> " + i);

            players.put(player, i);
        }

        radices = new int[(myRoundVotes.length + 1) * myPlayers.size()];

        for (int i = 0; i < myPlayers.size(); i++) {
            radices[i] = 2;
        }

        for (int i = myPlayers.size(); i < radices.length; i++) {
            radices[i] = 3;
        }

        this.createGameFilters();
    }

    public void assumeResistance(String player) {
        int playerOffset = players.get(player);

        String filterStr = StringUtils.leftPad("", radices.length, "*");
        filterStr = filterStr.substring(0, playerOffset) + "1" + filterStr.substring(playerOffset + 1);

        FilterList filter = FilterListBuilder.newInstance().setQuick(filterStr).getFilterList();

        filters.add(filter);
        logger.info("Adding assume resistance filter: " + filter);
    }

    // If one player accuses another players vote, remove the possiblity that the accuser is resistance and the vote is a fail
    public void applyVoteVouch(String voucher, String voucheeVote, int myRoundCount) {
        int playerOffset = players.get(voucher);
        int voucheeVoteOffset = players.get(voucheeVote) + (myRoundCount + 1) * players.size();

        String filterStr = StringUtils.leftPad("", radices.length, "*");

        filterStr = filterStr.substring(0, playerOffset) + "0" + filterStr.substring(playerOffset + 1);
        filterStr = filterStr.substring(0, voucheeVoteOffset) + "1" + filterStr.substring(voucheeVoteOffset + 1);

        FilterList filter = FilterListBuilder.newInstance().setQuick(filterStr).getFilterList();

        filters.add(filter);
        logger.info("Adding vote vouch filter: " + filter);
    }

    // If one player accuses another players vote, remove the possiblity that the accuser is resistance and the vote is pass
    public void applyVoteAccusation(String accuser, String accuseeVote, int myRoundCount) {
        int playerOffset = players.get(accuser);
        int accuseeVoteOffset = players.get(accuseeVote) + (myRoundCount + 1) * players.size();

        String filterStr = StringUtils.leftPad("", radices.length, "*");

        filterStr = filterStr.substring(0, playerOffset) + "0" + filterStr.substring(playerOffset + 1);
        filterStr = filterStr.substring(0, accuseeVoteOffset) + "0" + filterStr.substring(accuseeVoteOffset + 1);

        FilterList filter = FilterListBuilder.newInstance().setQuick(filterStr).getFilterList();

        filters.add(filter);
        logger.info("Adding vote accusation filter: " + filter);
    }

    // If one player accuses another of being a spy, remove the possiblity that they are both resistance
    public void applyAccustion(String player1, String player2) {
        int[] playerOffsets = new int[2];

        playerOffsets[0] = players.get(player1);
        playerOffsets[1] = players.get(player2);

        String filterStr = StringUtils.leftPad("", radices.length, "*");

        for (int playerOffset : playerOffsets) {
            filterStr = filterStr.substring(0, playerOffset) + "0" + filterStr.substring(playerOffset + 1);
        }

        FilterList filter = FilterListBuilder.newInstance().setQuick(filterStr).getFilterList();

        filters.add(filter);
        logger.info("Adding accusation filter: " + filter);
    }

    // If one player vouches for another, the vouchee cannot be a spy if the voucher is resistance.
    public void applyVouch(String voucher, String vouchee) {

        int voucherOffset = players.get(voucher);
        int voucheeOffset = players.get(vouchee);

        String filterStr = StringUtils.leftPad("", radices.length, "*");

        filterStr = filterStr.substring(0, voucherOffset) + "0" + filterStr.substring(voucherOffset + 1);
        filterStr = filterStr.substring(0, voucheeOffset) + "1" + filterStr.substring(voucheeOffset + 1);

        FilterList filter = FilterListBuilder.newInstance().setQuick(filterStr).getFilterList();

        filters.add(filter);
        logger.info("Adding vouch filter: " + filter);
    }

    public void createGameFilters() {
        // Create the rule that only x number of spies exist
        int[] playerRadices = new int[players.size()];

        String breakStr = "";
        for (int i = 0; i < players.size(); i++) {
            breakStr += '1';
            playerRadices[i] = 2;
        }

        String genFilterStr = "";
        int count = 0;
        while (true) {
            if (genFilterStr.equals(breakStr)) {
                break;
            }

            genFilterStr = "" + Integer.toBinaryString(count);
            genFilterStr = StringUtils.leftPad(genFilterStr, players.size(), '0');
            int combOnes = StringUtils.countMatches(genFilterStr, "1");
            if (combOnes != spies) {
                String filterStr = StringUtils.rightPad(genFilterStr, radices.length, "*");
                FilterList newFilter = FilterListBuilder.newInstance().setQuick(filterStr).getFilterList();

                filters.add(newFilter);
                logger.info("Adding base filter: " + newFilter);
            }

            count++;
        }

        //Create rules that resistance do not throw fails
        for (int i = 0; i < players.size(); i++) {
            String baseFilterStr = StringUtils.leftPad("", radices.length, "*");

            baseFilterStr = baseFilterStr.substring(0, i) + "0" + baseFilterStr.substring(i + 1);

            for (int j = 0; j < roundVotes.length; j++) {
                int missionOffset = (j + 1) * players.size() + i;

                String filterStr = baseFilterStr.substring(0, missionOffset) + "1" + baseFilterStr.substring(missionOffset + 1);

                FilterList newFilter = FilterListBuilder.newInstance().setQuick(filterStr).getFilterList();

                logger.info("Adding base filter: " + newFilter);
                filters.add(newFilter);
            }
        }
    }

    public Partition createRootPartition() {
        return PartitionBuilder.newInstance().setBlankWorld().setRadices(radices).addFilters(filters).getPartition();
    }

//    public Collection<FilterList> getFilters() {
//        return filters;
//    }
    public void playRound(Set<String> agents, int failures) {

        if (agents.size() != roundVotes[roundCount]) {
            logger.error("Round Vote Limit: " + roundVotes[roundCount]);
            logger.error("Players Sent: " + agents.size());
            throw new RuntimeException("Number of players on mission does not match mission count!");
        }

        Collection<FilterList> filters = new ArrayList<FilterList>();

        // Add filters due to vote outcome
//        HashSet<Integer> voteFilterOffsets = this.generateRoundPlayerFilterOffsets(agents);
//        Collection<String> voteFilterStrings = this.generateRoundPlayerFilterStrings(failures);
//        this.createRoundPlayerFilters(voteFilterStrings, voteFilterOffsets);

        this.createVoteFilters(agents, failures);

        //Add filters due to the players sent on the mission.
        this.createMissionFilters(agents);
        roundCount++;
    }

    // Filter out all combinations that do not contain the proper number of failure votes.
    public void createVoteFilters(Set<String> agents, int failures) {

        TreeMap<Integer, Integer> playerMap = new TreeMap<Integer, Integer>();

        Iterator<String> it = agents.iterator();
        for (int i = 0; i < agents.size(); i++) {
            String agent = it.next();

            int playerOffset = players.get(agent);
            playerMap.put(i, playerOffset);
        }

        int numRoundVotes = agents.size();

        String breakStr = "";
        for (int i = 0; i < numRoundVotes; i++) {
            breakStr += '1';
        }

        String permutationStr = "";
        int count = 0;
        while (!permutationStr.equals(breakStr)) {

            permutationStr = "" + Integer.toBinaryString(count);
            permutationStr = StringUtils.leftPad(permutationStr, numRoundVotes, '0');
            int combOnes = StringUtils.countMatches(permutationStr, "1");
            if (combOnes != failures) {

                char[] filterChars = permutationStr.toCharArray();
                Iterator<Integer> playerMapIt = playerMap.keySet().iterator();
                String filterStr = StringUtils.leftPad("", radices.length, "*");
                for (int i = 0; i < numRoundVotes; i++) {
                    int playerOffset = playerMap.get(playerMapIt.next());
                    char filterChar = filterChars[i];

                    int offset = (1 + roundCount) * players.size() + playerOffset;
                    filterStr = filterStr.substring(0, offset) + filterChar + filterStr.substring(offset + 1);
                }

                FilterList filter = FilterListBuilder.newInstance().setQuick(filterStr).getFilterList();

                logger.info("Adding vote filter: " + filter);
                filters.add(filter);
            }

            count++;
        }
    }

    //2 == player did not go on mission.  We remove these cases for the players that did/did not go.
    public void createMissionFilters(Set<String> agents) {

        BiMap<Integer, String> inversePlayers = players.inverse();

        for (int i = 0; i < players.size(); i++) {
            String agentStr = inversePlayers.get(i);

            if (agents.contains(agentStr)) {
                //If an agent is on the mission exclude the possibility they did not vote
                StringBuilder filterStringBuilder = new StringBuilder();

                int missionOffset = (roundCount + 1) * players.size() + i;
                filterStringBuilder.append(StringUtils.leftPad("", missionOffset, "*"));
                filterStringBuilder.append("2");
                filterStringBuilder.append(StringUtils.leftPad("", radices.length - missionOffset - 1, "*"));

                FilterList newFilter = FilterListBuilder.newInstance().setQuick(filterStringBuilder.toString()).getFilterList();

                logger.info("Adding Mission Filter: " + newFilter);
                filters.add(newFilter);
            } else {
                //If a player did not go on the mission, exclude the possibilities of them casting votes.
                for (int j = 0; j < 2; j++) {

                    StringBuilder filterStringBuilder = new StringBuilder();

                    int missionOffset = (roundCount + 1) * players.size() + i;
                    filterStringBuilder.append(StringUtils.leftPad("", missionOffset, "*"));
                    filterStringBuilder.append(j);
                    filterStringBuilder.append(StringUtils.leftPad("", radices.length - missionOffset - 1, "*"));

                    FilterList newFilter = FilterListBuilder.newInstance().setQuick(filterStringBuilder.toString()).getFilterList();

                    logger.info("Adding Mission Filter: " + newFilter);
                    filters.add(newFilter);
                }
            }
        }
    }

    public void createRoundPlayerFilters(Collection<String> voteFilterStrings, HashSet<Integer> voteFilterOffsets) {

        for (String voteFilterString : voteFilterStrings) {
            StringBuilder filterStringBuilder = new StringBuilder();
            int agentCount = 0;

            for (int i = 0; i < radices.length; i++) {

                if (voteFilterOffsets.contains(i)) {
                    String agentState = voteFilterString.substring(agentCount, agentCount + 1);
                    filterStringBuilder.append(agentState);
                    agentCount++;
                } else {
                    filterStringBuilder.append("*");
                }
            }

            FilterList newFilter = FilterListBuilder.newInstance().setQuick(filterStringBuilder.toString()).getFilterList();

            logger.info("Adding vote Filter: " + newFilter);
            filters.add(newFilter);
        }
    }

    //Used in conjunction with the output of 'generateVoteFilterStrings'
    //Gives the offset values to transform the strings into filters.
    public HashSet<Integer> generateRoundPlayerFilterOffsets(Set<String> agents) {

        HashSet<Integer> voteFilterOffsets = new HashSet<Integer>();

        for (String agent : agents) {
            int agentOffset = players.get(agent);
            voteFilterOffsets.add(agentOffset);
        }

        return voteFilterOffsets;
    }
}
