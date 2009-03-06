package mulan.classifier;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializedObject;

/**
 * Common interface for all multi-label learner types.
 *  
 * @author Jozef Vilcek
 */
public interface MultiLabelLearner {

	/**
	 * Returns a number of labels the learner is configured for.
	 * Label attributes are assumed to be the last ones in {@link Instances} training data.
	 * @return number of labels
	 */
	public int getNumLabels();
	
	/**
	 * Builds the learner model. 
	 *  
	 * @param instances set of training data, upon which the classifier should be build
	 * @throws Exception if classifier was not created successfully
	 */
	public void build(Instances instances) throws Exception;
	
	/**
	 * Creates a deep copy of the given learner using serialization.
	 *
	 * @return a deep copy of the learner
	 * @exception Exception if an error occurs
	 */
	public MultiLabelLearner makeCopy() throws Exception;


	/**
	 * Returns the output .
	 *
	 * @return a deep copy of the learner
	 * @exception Exception if an error occurs
	 */
    public MultiLabelOutput makePrediction(Instance instance) throws Exception;
}
