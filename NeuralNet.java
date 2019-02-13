import java.util.Random;
import java.text.DecimalFormat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

public class NeuralNet{
	//Neural Net Class
	private String path;//This is the path of the text file that saves the info of the neural network
	private double neurons[][]; //JAGGED "2x2" Array, used for the values of the neurons in each layer
	private double weights[][][];//JAGGED "3x3" Array, used to store the weights of the connections in between the layers
	private double biases[][];
	private int numLayers;
	
	/***********************************
	 ***********************************   Constructors:
	 ***********************************/
	 //-------------------------------------------------------------------------------------------------------
	public NeuralNet(String Net_Path) //This constructor is for loading a Neural net from a .nnet file
	{
		path = Net_Path;
	}
	//----------------------------------------------------------------------------------------------------------
	public NeuralNet(int layerSizes[]){ //Sets up a NEW neural net and initializes neurons
		Random rnd = new Random();
		numLayers = layerSizes.length;
		//************************************************************************************************
		if(numLayers > 1)
		{
			neurons = new double[numLayers][];                //Error checking to make sure that numLayers == layerSizes.length && numLayers >0
			biases = new double[numLayers][];
		}
		else
			throw new java.lang.Error("The Neural Net must have more than one Layer. < Ex: ...= new NeuralNet({5}) <-- No Good >");
			
		if(numLayers != layerSizes.length)
			throw new java.lang.Error("Number of layers of the Net must be equal to layerSizes.length. Ex: ... = new NeuralNet(numLayers, array) <- array.length must be = to numLayers");
		//*************************************************************************************************
		for (int i = 0; i < layerSizes.length; i++){
			if(layerSizes[i] > 0)
			{
				neurons[i] = new double[layerSizes[i]];
				biases[i] = new double [layerSizes[i]];
			}
			else
				throw new java.lang.Error("The number of Neurons in a layer can't be zero");//******Error******//
		}
		
		weights = new double [numLayers - 1][][];                                  //(comment below)
		
		for(int i = 0; i < numLayers - 1; i++)
		{
			weights[i] = new double[neurons[i].length][neurons[i + 1].length];    //Initializing the size and random values of the connections between neurons
		}
		
		for(int i = 0; i < numLayers-1; i++){
			for(int u = 0; u < weights[i].length; u++){
				for (int p = 0; p < weights[i][u].length; p++){                   //(comment above)
					double xx = Math.floor(rnd.nextDouble() * 100) / 100;
					while(xx <= 0){
						xx = Math.floor(rnd.nextDouble() * 100) / 100;
					}
					weights[i][u][p] = xx;
				}
			}
		}
		
		for(int i = 0; i < biases.length; i++)
			for(int j = 0; j < biases[i].length; j++)
			{
				int a = rnd.nextBoolean() ? -1 : 1;
				biases[i][j] = rnd.nextDouble() * a;
			}
	}
	/***********************************
	 ***********************************   Methods Start:
	 ***********************************/
	//-------------------------------------------------------------------------------------------------------------------------
	public void printNeurons(){ //Print the current values for each neuron for each layer
		DecimalFormat fmt = new DecimalFormat(".00");
		for(int i = 0; i < neurons.length; i++){
			for(int u = 0; u < neurons[i].length; u++){
				System.out.println (fmt.format(neurons[i][u]));
			}
			System.out.println ("");
		}
	}
	//-------------------------------------------------------------------------------------------------------------------------
	public void printBiases(){ //Print the current values for each neuron for each layer
		DecimalFormat fmt = new DecimalFormat(".00");
		for(int i = 0; i < biases.length; i++){
			for(int u = 0; u < biases[i].length; u++){
				System.out.println (fmt.format(biases[i][u]));
			}
			System.out.println ("");
		}
	}
	//-------------------------------------------------------------------------------------------------------------------------
	public void printWeights(){ //prints the current values for the weights of the neurons between each layers in a multiline grid
		DecimalFormat fmt = new DecimalFormat(".00");
		System.out.println ("Layer 1");
		for(int i = 0; i < numLayers-1; i++){
			for(int u = 0; u < weights[i].length; u++){
				for (int p = 0; p < weights[i][u].length; p++){
					System.out.print (fmt.format(weights[i][u][p]) + "  ");
				}
				System.out.println("");
			}
			if(i + 1 < numLayers - 1)
				System.out.println (" \nLayer " + (i + 2));
		}
	}
	//-------------------------------------------------------------------------------------------------------------------------
	private double map(double x, double in_min, double in_max, double out_min, double out_max)
	{
  		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
	//-------------------------------------------------------------------------------------------------------------------------
	public static double sigmoid(double x) //Function used to turn values into a range in between 0 && 1
	{
    	return 1 / (1 + Math.exp(-x));
	}
	//-------------------------------------------------------------------------------------------------------------------------
	private double round(double x) //Function used to turn values into a range in between 0 && 1
	{
    	return Math.floor(x * 100) / 100;
	}
	//-------------------------------------------------------------------------------------------------------------------------
	public int forwardPropogate(double inputs[])
	{
		//*******************************************************************************************************
		if(inputs.length != neurons[0].length)
			throw new java.lang.Error("The input array's length must be equal to the number of input neurons"); //Error Checking
		//*******************************************************************************************************
		for(int i = 0; i < neurons[0].length; i++)
			neurons[0][i] = inputs[i];                 //Set First Layer to Input
			
		//**********************************************************************************
		for(int i = 0; i < numLayers - 1; i++)
		{
			for(int a = 0; a < neurons[i + 1].length; a++)
			{
				int p = 0;
				for (p = 0; p < neurons[i].length; p++)								//Multiplying through, actual propogation
				{
					neurons[i + 1][a] += neurons[i][p] * weights[i][p][a];
				}
				neurons[i+1][a] = sigmoid(neurons[i+1][a] + biases[i+1][a]);
			}
		}
		
		double largest = neurons[numLayers - 1][0];
		int largestIndex = 0;
		for(int i = 1; i < neurons[numLayers - 1].length; i++)
			if(largest < neurons[numLayers - 1][i])					//Finding largest index to return: aka the value the net sees as the answer
			{
				largest = neurons[numLayers-1][i];
				largestIndex = i;
			}
		return largestIndex;
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static void writeToFile(String data) {
        try {
            Files.write(Paths.get(System.getProperty("user.dir") + "/name.NNet"), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	//-------------------------------------------------------------------------------------------------------------------------
	public String readFile()
	{
		File file = new File(System.getProperty("user.dir") + "/name.NNet"); 
		String toOut = "";
  		try{
  			BufferedReader br = new BufferedReader(new FileReader(file)); 
  		
		String st; 
		while ((st = br.readLine()) != null) 
			toOut += st;
  		}
  		catch(IOException e)
  		{e.printStackTrace();}
		return	toOut;
	}
}