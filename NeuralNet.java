import java.util.Random;
import java.text.DecimalFormat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.StringTokenizer;

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
		load();
		numLayers = weights.length + 1;
		neurons = new double[numLayers][];
		
		neurons[0] = new double[weights[0].length];
		neurons[numLayers - 1] = new double[weights[numLayers - 2][0].length];
		
		for(int i = 1; i < numLayers - 1; i++)
			neurons[i] = new double[weights[i].length];
	}
	//----------------------------------------------------------------------------------------------------------
	public NeuralNet(int layerSizes[], boolean shouldLoadNetIfAvailible){ //Sets up a NEW neural net and initializes neurons
		if(shouldLoadNetIfAvailible)
		{
			File dir = new File(System.getProperty("user.dir"));
			 for (File file : dir.listFiles()) {
			   if (file.getName().toLowerCase().endsWith((".nnet"))) {
			     path = file.getName();
			     
			 	 load();
			 	 numLayers = weights.length + 1;
				 neurons = new double[numLayers][];
				
				 neurons[0] = new double[weights[0].length];
				 neurons[numLayers - 1] = new double[weights[numLayers - 2][0].length];
				
				 for(int i = 1; i < numLayers - 1; i++)
					neurons[i] = new double[weights[i].length];
			     
			     break;
			   }
  			}
		}
		else
		{	
			Random rnd = new Random();
			numLayers = layerSizes.length;
			
			//************************************************************************************************
			if(numLayers > 2)
			{
				neurons = new double[numLayers][];                //Error checking to make sure that numLayers == layerSizes.length && numLayers >0
				biases = new double[numLayers][];
			}
			else
				throw new java.lang.Error("The Neural Net must have more than two layers");
				
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
				for(int j = 0; j < biases[i].length; j++)   //Intitializing Biases
				{
					int a = rnd.nextBoolean() ? -1 : 1;
					biases[i][j] = rnd.nextDouble() * a;
				}
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
	public void load()
	{
		File a;
		//*************Error Checking*******************
		if(!path.toLowerCase().contains(".nnet"))
			throw new java.lang.Error("File must be a .NNet file");
			
		if(path.indexOf("/") == -1 && path.indexOf("\\") == -1)
			path = System.getProperty("user.dir") + "\\" + path;
		
		if(path != null)
			a = new File(path);
		
		else
			throw new java.lang.Error("File Path does not exist. Use load with parameter if the NeuralNet object was not created with only file path");
		if(!a.exists())
			throw new java.lang.Error("The file: " + path + " does not exist or could not be found");
		//****************************************************
		
		String doc = readFileFullPath(path);
		
		String[] f = doc.split(" ");
		String[][] s = new String[f.length][];
		
		for(int i = 0; i < f.length; i++)
			s[i] = f[i].split("V");
			
		String[][][] t = new String[s.length][][];
		
		for(int i = 0; i < s.length; i++)
			for(int y = 0; y < s[i].length; y++)
			{
				t[i] = new String[s[i].length][];
			}
				
		for(int i = 0; i < s.length; i++)
			for(int y = 0; y < s[i].length; y++)
				t[i][y] = s[i][y].split("Z");

						
		weights = new double[t.length][][];
		
		for(int i = 0; i < t.length; i++)
			for(int y = 0; y < t[i].length; y++)
					weights[i] = new double[t[i].length][];
		
		for(int i = 0; i < t.length; i++)
			for(int y = 0; y < t[i].length; y++)
				for(int x = 0; x < t[i][y].length; x++)
					weights[i][y] = new double[t[i][y].length];		
				
			for(int i = 0; i < t.length; i++)
				for(int y = 0; y < t[i].length; y++)
					for(int j = 0; j < t[i][y].length; j++)
					{
						weights[i][y][j] = Double.valueOf(t[i][y][j]);
					}
	}
	//-------------------------------------------------------------------------------------------------------------------------
	public void load(String file)
	{
		File a;
		//*************Error Checking*******************
		if(!file.toLowerCase().contains(".nnet"))
			throw new java.lang.Error("File must be a .NNet file");
			
		if(file.indexOf("/") == -1 && file.indexOf("\\") == -1)
			file = System.getProperty("user.dir") + "\\" + path;
		
		if(path != null)
			a = new File(file);
		
		else
			throw new java.lang.Error("File Path does not exist. Use load with parameter if the NeuralNet object was not created with only file path");
		if(!a.exists())
			throw new java.lang.Error("The file: " + path + " does not exist or could not be found");
		//****************************************************
		
		String doc = readFileFullPath(path);
		
		String[] f = doc.split(" ");
		String[][] s = new String[f.length][];
		
		for(int i = 0; i < f.length; i++)
			s[i] = f[i].split("V");
			
		String[][][] t = new String[s.length][][];
		
		for(int i = 0; i < s.length; i++)
			for(int y = 0; y < s[i].length; y++)
			{
				t[i] = new String[s[i].length][];
			}
				
		for(int i = 0; i < s.length; i++)
			for(int y = 0; y < s[i].length; y++)
				t[i][y] = s[i][y].split("Z");

						
		weights = new double[t.length][][];
		
		for(int i = 0; i < t.length; i++)
			for(int y = 0; y < t[i].length; y++)
					weights[i] = new double[t[i].length][];
		
		for(int i = 0; i < t.length; i++)
			for(int y = 0; y < t[i].length; y++)
				for(int x = 0; x < t[i][y].length; x++)
					weights[i][y] = new double[t[i][y].length];		
				
			for(int i = 0; i < t.length; i++)
				for(int y = 0; y < t[i].length; y++)
					for(int j = 0; j < t[i][y].length; j++)
					{
						weights[i][y][j] = Double.valueOf(t[i][y][j]);
					}
	}
	//-------------------------------------------------------------------------------------------------------------------------
	public void save(String filePathAndName)
	{
		String toSave = "";
		for(int i = 0; i < weights.length; i++)
		{
			for(int x = 0; x < weights[i].length; x++)
			{
				for(int y = 0; y < weights[i][x].length; y++)
				{
					toSave += String.valueOf(weights[i][x][y]);
					if(y != weights[i][x].length - 1)
						toSave += "Z";
				}
				if(x != weights[i].length - 1)
					toSave += "V";
			}
			if(i != weights.length - 1)
				toSave += " ";
		}
		
		File a;
		//*************Error Checking*******************
		if(!filePathAndName.toLowerCase().contains(".nnet"))
			throw new java.lang.Error("File must be a .NNet file");
			
		if(filePathAndName.indexOf("/") == -1 && filePathAndName.indexOf("\\") == -1)
			path = System.getProperty("user.dir") + "\\" + path;
			
		writeToFileFullPath(toSave,filePathAndName);
	}
	//-------------------------------------------------------------------------------------------------------------------------
	public void save()
	{
		String toSave = "";
		for(int i = 0; i < weights.length; i++)
		{
			for(int x = 0; x < weights[i].length; x++)
			{
				for(int y = 0; y < weights[i][x].length; y++)
				{
					toSave += String.valueOf(weights[i][x][y]);
					if(y != weights[i][x].length - 1)
						toSave += "Z";
				}
				if(x != weights[i].length - 1)
					toSave += "V";
			}
			if(i != weights.length - 1)
				toSave += " ";
		}
		
		File a;
		//*************Error Checking*******************
		if(!path.toLowerCase().contains(".nnet"))
			throw new java.lang.Error("File must be a .NNet file");
			
		if(path.indexOf("/") == -1 && path.indexOf("\\") == -1)
			path = System.getProperty("user.dir") + "\\" + path;
		
		if(path != null)
			a = new File(path);
		else
			throw new java.lang.Error("File Path does not exist. Use load with parameter if the NeuralNet object was not created with only file path");
		
		writeToFileFullPath(toSave,path);
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
	private static double sigmoid(double x) //Activation function. reLU is also availible
	{
    	return 1 / (1 + Math.exp(-x));
	}
	//-------------------------------------------------------------------------------------------------------------------------
	private static double sigmoidDerivative(double x) //Derivative funtion of sigmoid
	{
		return sigmoid(x) * (1 - sigmoid(x));
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
				neurons[i+1][a] = sigmoid(neurons[i+1][a]/* + biases[i+1][a]*/);
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
		//-------------------------------------------------------------------------------------------------------------------------
	//"Overload" that returns the arrays of output
	public double[] forwardPropogateGetOutputLayer(double inputs[])
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
				neurons[i+1][a] = sigmoid(neurons[i+1][a]/* + biases[i+1][a]*/);
			}
		}
		return neurons[numLayers - 1]; //Returns output layer
	}
	//-------------------------------------------------------------------------------------------------------------------------
	private double[][] getNetInput()
	{
		double[][] g = getBlankArraySameAsNeurons();
		for(int i = 0; i < numLayers - 1; i++)
		{
			for(int a = 0; a < g[i + 1].length; a++)
			{
				int p = 0;
				for (p = 0; p < g[i].length; p++)								//Multiplying through, actual propogation
				{
					g[i + 1][a] += neurons[i][p] * weights[i][p][a];
				}
			}
		}
		return g;
	}
	//-------------------------------------------------------------------------------------------------------------------------
	private double[][] getBlankArraySameAsNeurons()
	{
		double[][] a = new double[numLayers][];
		for(int i = 0; i < a.length; i++)
		{
			a[i] = new double[neurons[i].length];
		}
		return a;
	}
	//-------------------------------------------------------------------------------------------------------------------------
	private double[][][] getBlankArraySameAsWeights()
	{
		double[][][] a = new double[numLayers - 1][][];
		for(int i = 0; i < numLayers - 1; i++)
		{
			a[i] = new double[neurons[i].length][neurons[i+1].length];
		}
		return a;
	}
	//-------------------------------------------------------------------------------------------------------------------------
	public void teach(double[] inputs, double[] expectedOutputs)
	{
		backPropogatate(forwardPropogateGetOutputLayer(inputs), expectedOutputs);
	}
	//-------------------------------------------------------------------------------------------------------------------------
	public void backPropogatate(double[] output, double[] desiredOutput)
	{
		
		final double RATE = .5;
		//*******************************************************************************************************
		if(output.length != desiredOutput.length)
			throw new java.lang.Error("Output array and desired output array must be the same length");				//Error Checking
		if(output.length != neurons[numLayers-1].length)
			throw new java.lang.Error("The output array and desiredOutput array must be the same size as the neural net's output layer");
		//*******************************************************************************************************
		
		double[] dEOutput = new double[neurons[numLayers-1].length]; //This array will store the error derivatives
		
		for(int i = 0; i < dEOutput.length; i++) //Stores the error derivatives of the first layer
		{
			dEOutput[i] = output[i] - desiredOutput[i]; //formula for derivative of error is: output - target
		}

		double[][] netInput = getNetInput(); //This will get the net input for all the neurons (activation function not applied)
		
		double[][] dEdNI = getBlankArraySameAsNeurons(); //Stores the derivative of the Error with respect to the neurons net input
		
		double[][] dEdNO = getBlankArraySameAsNeurons(); //Stores the derivative of the error with respect to the neurons output
		
		double[][][] dEdW = getBlankArraySameAsWeights(); //Derivative of error with respect to weights
		
		double[][][] updatedWeights = getBlankArraySameAsWeights(); //We can't do calculations with updated weights so we must store them
		
		
		
		for(int i = 0; i < dEdNI[numLayers - 1].length; i++)
		{
			dEdNI[numLayers - 1][i] = sigmoidDerivative(netInput[numLayers-1][i]) * dEOutput[i]; //Setting the derivative of error with respect to net input of neuron for output neurons
		}
		
		for(int i = 0; i < dEdW[dEdW.length - 1].length;i++)
			for(int y = 0; y < dEdW[dEdW.length - 1][i].length; y++)
			{
				dEdW[dEdW.length -1][i][y] = neurons[numLayers - 2][i] * dEdNI[numLayers - 1][y]; //This finds the derivative of error with respect to the weights for the outer layer
			}
			
		for(int i = 0; i < neurons[numLayers - 2].length; i++) //Before starting the hidden layers we need to find the derivative of the Error with respect Neron's output 
			for(int y = 0; y < neurons[numLayers - 1].length; y++)
			{
				dEdNO[numLayers - 2][i] += dEdNI[numLayers - 1][y] * weights[numLayers - 2][i][y]; //Used for next layer to calculate off of
			}
		
		for(int i = 0; i < neurons[numLayers-2].length; i++)
			for(int y = 0; y < neurons[numLayers - 1].length; y++)
			{
				updatedWeights[numLayers - 2][i][y] = weights[numLayers - 2][i][y] - RATE * dEdW[numLayers - 2][i][y];
			}
		//*****Hidden layer calculation time********
		
		for(int i = numLayers - 3; i >= 0; i--) //Iterate through layers from 0 to the number of layers - 3 (bc the last layer is done, which is numLayers - 2)
		{
			for(int y = 0; y < neurons[i].length; y++)//Now we fill dEdNI for layer
			{
				dEdNI[i][y] = sigmoidDerivative(netInput[i][y]) * dEdNO[i][y];
			}
			
			for(int y = 0; y < neurons[i].length; y++)//Now we fill dEdW for each layer
			{
				for(int x = 0; x < weights[i][y].length; x++)
				{
					dEdW[i][y][x] = neurons[i][y] * dEdNI[i + 1][x];
				}
				
			}
			
			for(int y = 0; y < neurons[i].length; y++)//Now we fill dEdNO for each layer
			{
				for(int x = 0; x < neurons[i+1].length; x++)
				{
					dEdNO[i][y] += weights[i][y][x] * dEdNI[i+1][x];
				}
			}
			
			for(int y = 0; y < neurons[i].length; y++)//Add to the new weights
			{
				for(int x = 0; x < weights[i][y].length; x++)
				{
					updatedWeights[i][y][x] = weights[i][y][x] - RATE * dEdW[i][y][x];
				}
			}
		}
		
		weights = updatedWeights;
		//Now we need to work on hidden layers
		
		
		//TODO: The actual calculations: https://mattmazur.com/2015/03/17/a-step-by-step-backpropagation-example/
		
		//Calculate and store error for output layers, add them up for the total error
			// .5(target1 - out1)^2 + .5(target2 - out2)^2 + ......
			//          E1                     E2                 Ex
			
		//Find the partial derivative of the total error of the output layer with respect to the neuron
			// (out - target)
			
		//***********Output layer Weight changing below****************
		
		//Find derivative of:
			//Total error of layer with respect to the neuron   -> already done above 
			//Activation function   -> Already done in functions "sigmoidDerivative()" and "reLUDerivative()" -> pass ((n[x][y] * w[x][y][z] + n[...]][...] * w[...][...][...]) + b[x][y])
			//net input of the neuron with respect to the weight -> It is just the neuron that is connected to the weight on the left side. 
								//ie: if n1 is connected to n2 by w1, then n2 with respect to w1 is n1. Structure: n1-->--w1-->--n2. 
		
			//Then multiply those derivatives to get the derivative of Etotal with respect to the weight
			
		//Lastly store (in a new array because we don't want to change weights yet) oldWeight - (Learning Constant)*(derivative of Etotal with respect to weight) 
		
		//Repeat for all weights connecting to output layer
		
		
		
		//******************************Hidden Layer Weight changing below***********************************
		
		// Goal is to find derivative of Etotal with respect to the weight connecting hidden layers to other hidden layers/input layer
		
			//Derivative of Etotal with respect to weight is the multiplication of all the derivatives of:
				// Etotal with respect to the neuron the weights are conneted to
				// Activation function -> Already done in functions "sigmoidDerivative()" and "reLUDerivative()" -> pass ((n[x][y] * w[x][y][z] + n[...]][...] * w[...][...][...]) + b[x][y])
				// net input to neuron with respect to the weight
				
				
			//**************************Finding Etotal with respect to the Neuron the weights are connected to*******************
				// Is equal to derivative of E1 with respect to the neuron the weights are connected to + derivative of E2 with respect to the neuron the weights are connected to + ...
					// dE1/dN + dE2/dN + .....
					
						//To find dE1/dN
						
							//dE1/dN = dE1/dnetInput * dnetInput/dN
							
								//dE1/dnetInput is already found bc it is equal to dE1/dN * dN/dnetInput
									//->dE1/dN = (out - target) or the derivative of .5(target - out)^2 (which is just target - out idk which one yet lol
									//->dN/dnetInput = derivative of activation function
									
								//Then dnetInput/dN = the weight that connects it to the neuron associated with E1
								
							//Lastly multiply the two to get dE1/dN
							
						//******To find dE2/dN and so on*********
							//Repeat earlier steps
					//** Lastly add dE1/dN + ....... --> This gives the derivative of Etotal with respect to the neuron the weights are connected to
					
			//***********************************************************************************************************************
			
			// Next is derivative of activation function which is already done
			
			// Next is the derivative of the net input with respect to the weight
				// This is just the first layer that connects to the weight. So if the setup is n1-->--w1-->--n2, it is just n1
				
		//*** Multiply all the derivatives found to get dEtotal/dw
		
		//****Lastly, update the weight in separate container
			//-> w1 = w1 - (learningRate)(dEtotal/dw)
			
		//Last step (if anyone actually reads this) get a girlfriend or maybe go outside
					
				
	//**********REPEAT*********************		
			
	}
	//-----------------------------------------------------------------------------------------------------------------------
	private static void writeToFile(String data, String fileName) {
		File a;
		if(fileName.toLowerCase().contains(".nnet"))
			a = new File(System.getProperty("user.dir") + "/" + fileName);               //Make file with or without extension
		else
			a = new File(System.getProperty("user.dir") + "/" + fileName + ".NNet");
			
			
        try {
            Files.write(Paths.get(a.getPath()), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	//-------------------------------------------------------------------------------------------------------------------------
	//This is an overloaded method with the filepath
	private static void writeToFile(String data, String fileName, String filePathNoSlash) {
		File a;
		if(fileName.toLowerCase().contains(".nnet"))
			a = new File(filePathNoSlash + "/" + fileName);				//Make file with or without extension
		else
			a = new File(filePathNoSlash + "/" + fileName + ".NNet");
			
		
        try {
            Files.write(Paths.get(a.getPath()), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	//-------------------------------------------------------------------------------------------------------------------------
	private static void writeToFileFullPath(String data, String fileNameAndPath) {
			File a;
		a = new File(fileNameAndPath);
			
		if(!a.exists())
			try
			{
				a.createNewFile();
			}
			catch(Exception e)
			{
				throw new java.lang.Error("The file: " + fileNameAndPath + " could not be created. Please make sure a correct location, and extension are included in the String");
			}
		
			
        try {
            Files.write(Paths.get(a.getPath()), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	//-------------------------------------------------------------------------------------------------------------------------
	private String readFile(String filePathNoSlash, String fileName)
	{
		File file;
		if(fileName.toLowerCase().contains(".nnet"))
			file = new File(filePathNoSlash + "/" + fileName);				//Make file with or without extension
		else
			file = new File(filePathNoSlash + "/" + fileName + ".NNet");
			
		if(!file.exists())
			throw new java.lang.Error("The file: " + file.getName() + " could not be located."); //Throw error if file doesnt exist
		
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
	//-------------------------------------------------------------------------------------------------------------------------
	//Overload
	private String readFile(String fileName)
	{
		File file;
		if(fileName.toLowerCase().contains(".nnet"))
			file = new File(System.getProperty("user.dir") + "/" + fileName);               //Make file with or without extension
		else
			file = new File(System.getProperty("user.dir") + "/" + fileName + ".NNet");
			
		if(!file.exists())
			throw new java.lang.Error("The file: " + file.getName() + " could not be located."); //Throw error if file doesnt exist
				
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
	//-------------------------------------------------------------------------------------------------------------------------
	//Overload
	private String readFileFullPath(String filePathWithName)
	{
		File file;
		file = new File(filePathWithName);
			
		if(!file.exists())
			throw new java.lang.Error("The file: " + file.getName() + " could not be located."); //Throw error if file doesnt exist
				
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
	//-------------------------------------------------------------------------------------------------------------------------
	private double reLU(double a) //Activation function. Sigmoid is also availible
	{
		return Math.max(0.0, a);
	}
	//-------------------------------------------------------------------------------------------------------------------------
	private double reLUDerivative(double a) //Derivative of reLU activation Function
	{
		if (a > 0)
      		return 1.0;
   		else
      		return 0.0;
	}
}