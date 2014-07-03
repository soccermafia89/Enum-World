package ethier.alex.world.core;

public class TestUtils {
	
	public static boolean compareDoubles(double double1, double double2, double precision) {
		double diff = double1 - double2;
		
		if(diff < 0) {
			diff = -1 * diff;
		}
		
		if(diff < precision) {
			return true;
		} else {
			return false;
		}
	}
}
