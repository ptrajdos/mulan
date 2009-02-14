package mulan.evaluation;

import java.util.List;

import mulan.classifier.Bipartition;
import weka.core.Utils;

public class LabelBasedMeasures {

    // Macro and Micro averaged measures
    protected double[] recall    = new double[2];
    protected double[] precision = new double[2];
    protected double[] fMeasure  = new double[2];
    protected double[] accuracy  = new double[2];

    //Per label measures
	protected double[] labelRecall;
	protected double[] labelPrecision;
	protected double[] labelFMeasure;
	protected double[] labelAccuracy;


    protected LabelBasedMeasures(LabelBasedMeasures[] arrayOfMeasures) {

		int numLabels  = arrayOfMeasures[0].labelAccuracy.length;
		labelAccuracy  = new double[numLabels];
		labelRecall    = new double[numLabels];
		labelPrecision = new double[numLabels];
		labelFMeasure  = new double[numLabels];

		for(LabelBasedMeasures measures : arrayOfMeasures)
		{
            for (Averaging type : Averaging.values()) {
                accuracy[type.ordinal()]  += measures.getAccuracy(type);
                recall[type.ordinal()]    += measures.getRecall(type);
                precision[type.ordinal()] += measures.getPrecision(type);
                fMeasure[type.ordinal()]  += measures.getFMeasure(type);
            }

			for(int j=0; j<numLabels; j++)
			{
				labelAccuracy[j]  += measures.getLabelAccuracy(j);
				labelRecall[j]    += measures.getLabelRecall(j);
				labelPrecision[j] += measures.getLabelPrecision(j);
				labelFMeasure[j]  += measures.getLabelFMeasure(j);
			}
		}

		int arrayLength = arrayOfMeasures.length;
        for (Averaging type : Averaging.values()) {
            accuracy[type.ordinal()]  /= arrayLength;
            recall[type.ordinal()]    /= arrayLength;
            precision[type.ordinal()] /= arrayLength;
            fMeasure[type.ordinal()]  /= arrayLength;
        }

		for(int i=0; i<numLabels; i++)
		{
			labelAccuracy[i]  /= arrayLength;
			labelRecall[i]    /= arrayLength;
			labelPrecision[i] /= arrayLength;
			labelFMeasure[i]  /= arrayLength;
		}

    }

	protected LabelBasedMeasures(List<ModelEvaluationDataPair<Bipartition>> predictions) {
        computeMeasures(predictions);
    }

    private void computeMeasures(List<ModelEvaluationDataPair<Bipartition>> predictions) {
        int numLabels = predictions.get(0).getNumLabels();

        //Counters are doubles to avoid typecasting
        //when performing divisions. It makes the code a
        //little cleaner but:
        //TODO: run performance tests on counting with doubles
        double[] falsePositives = new double[numLabels];
        double[] truePositives  = new double[numLabels];
        double[] falseNegatives = new double[numLabels];
        double[] trueNegatives  = new double[numLabels];

        //Count TP, TN, FP, FN
        for (ModelEvaluationDataPair<Bipartition> pair : predictions) 
		{
            for (int j = 0; j < numLabels; j++)
            {
                boolean actual = pair.getTrueLabels().get(j);
                boolean predicted = pair.getModelOutput().getBipartition().get(j);

                if (actual && predicted)
                    truePositives[j]++;
                else if (!actual && !predicted)
                    trueNegatives[j]++;
                else if (predicted)
                    falsePositives[j]++;
                else
                    falseNegatives[j]++;
            }
        }

        // Evaluation measures for individual labels
        labelAccuracy  = new double[numLabels];
        labelRecall    = new double[numLabels];
        labelPrecision = new double[numLabels];
        labelFMeasure  = new double[numLabels];

        //Compute macro averaged measures
        int numInstances = predictions.size();
        for(int i = 0; i < numLabels; i++)
        {
            labelAccuracy[i] = (truePositives[i] + trueNegatives[i]) / numInstances;

            labelRecall[i] = truePositives[i] + falseNegatives[i] == 0 ? 0
                            :truePositives[i] / (truePositives[i] + falseNegatives[i]);

            labelPrecision[i] = truePositives[i] + falsePositives[i] == 0 ? 0
                            :truePositives[i] / (truePositives[i] + falsePositives[i]);

            labelFMeasure[i] = computeF1Measure(labelPrecision[i], labelRecall[i]);
        }
        accuracy[Averaging.MACRO.ordinal()]  = Utils.mean(labelAccuracy);
	    recall[Averaging.MACRO.ordinal()]    = Utils.mean(labelRecall);
	    precision[Averaging.MACRO.ordinal()] = Utils.mean(labelPrecision);
	    fMeasure[Averaging.MACRO.ordinal()]  = Utils.mean(labelFMeasure);

	    //Compute micro averaged measures
	    double tp = Utils.sum(truePositives);
	    double tn = Utils.sum(trueNegatives);
	    double fp = Utils.sum(falsePositives);
	    double fn = Utils.sum(falseNegatives);

	    accuracy[Averaging.MICRO.ordinal()]  = (tp + tn) / (numInstances * numLabels);
	    recall[Averaging.MICRO.ordinal()]    = tp + fn == 0 ? 0 : tp / (tp + fn);
	    precision[Averaging.MICRO.ordinal()] = tp + fp == 0 ? 0 : tp / (tp + fp);
	    fMeasure[Averaging.MICRO.ordinal()]  = computeF1Measure(precision[Averaging.MICRO.ordinal()], recall[Averaging.MICRO.ordinal()]);
    }


	protected void compute(List<ModelEvaluationDataPair<Bipartition>> predictionData){
	//	throw new NotImplementedException();
	}
	
	protected void compute(ModelCrossValidationDataSet<Bipartition> crossValPredictionDataSet){
	//	throw new NotImplementedException();
	}
	
	public ConfidenceLabelBasedMeasures getConfidenceLabelBasedMeasures(){
        return null;
	//	throw new NotImplementedException();
	}
	
	public double getAccuracy(Averaging averagingType){
		return accuracy[averagingType.ordinal()];
	}
	
	public double getFMeasure(Averaging averagingType){
		return fMeasure[averagingType.ordinal()];
	}
	
	public double getRecall(Averaging averagingType){
		return recall[averagingType.ordinal()];
	}
	
	public double getPrecision(Averaging averagingType){
		return precision[averagingType.ordinal()];
	}


	public double getLabelAccuracy(int label){
		return labelAccuracy[label];
	}

	public double getLabelFMeasure(int label){
		return labelFMeasure[label];
	}

	public double getLabelRecall(int label){
		return labelRecall[label];
	}

	public double getLabelPrecision(int label){
		return labelPrecision[label];
	}

    private double computeF1Measure(double precision, double recall)
	{
	    if (Utils.eq(precision + recall, 0))
            return 0;
	    else
            return (2 * precision * recall) / (precision + recall);
	}
}
