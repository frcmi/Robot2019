package frc.robot.lib.trajectory;

// sammck: commented out this class to fix build

/*
import frc.robot.lib.util.SnailMath;
import org.ejml.UtilEjml;
import org.ejml.alg.dense.decomposition.eig.EigenPowerMethod;
import org.ejml.alg.dense.mult.VectorVectorMult;
import org.ejml.data.Complex64F;
import org.ejml.data.DenseMatrix64F;
import org.ejml.data.Eigenpair;
import org.ejml.factory.EigenDecomposition;
*/

public class CorrectPath {

    // public Complex64F[] path;

    // Let f = y coord. of point, d = x coord. of point
    // path = findRoots(5*a*a, 9*a*b, 8*a*c+4*b*b, 7*b*c, 3*c*c, -f*5*a, -f*4*b, -f*3*c, -1, d);
        /**
         * <p>
         * Given a set of polynomial coefficients, compute the roots of the polynomial.  Depending on
         * the polynomial being considered the roots may contain complex number.  When complex numbers are
         * present they will come in pairs of complex conjugates.
         * </p>
         *
         * @param coefficients Coefficients of the polynomial.
         * @return The roots of the polynomial
         */
	/*
        public static Complex64F[] findRoots(double... coefficients) {
            int N = coefficients.length-1;
    
            // Construct the companion matrix
            DenseMatrix64F c = new DenseMatrix64F(N,N);
    
            double a = coefficients[N];
            for( int i = 0; i < N; i++ ) {
                c.set(i,N-1,-coefficients[i]/a);
            }
            for( int i = 1; i < N; i++ ) {
                c.set(i,i-1,1);
            }
    
            // use generalized eigenvalue decomposition to find the roots
            EigenDecomposition<DenseMatrix64F> evd =  DecompositionFactory.eigGeneral(N, false);
    
            evd.decompose(c);
    
            Complex64F[] roots = new Complex64F[N];
    
            for( int i = 0; i < N; i++ ) {
                roots[i] = evd.getEigenvalue(i);
            }
    
            return roots;
        }
	*/

}
