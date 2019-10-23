
public class Net_Runner
{
	public static void main (String[] args)
	{
		int b[] = {3,2,3}; //This is the setup for the net: the physica net looks like the picture at the bottom
		NeuralNet net = new NeuralNet(b, true);
		double c[] = {.45, .22, .11}; //Random input values.
		net.forwardPropogate(c); //This is to fill all the neurons with values, so the initial state can be printed
		net.printNeurons();
		double d[] = {1, 0.0, 0.0}; //Expected outputs
		for(int i = 0; i < 100; i++) //This is to teach 100 times
		{
			net.teach(c,d); //Teaches the AI with the inputs C, and expected output D. Each time this is called the output becomes closer to expected outputs
		}
		
		System.out.println ("\n***Old Neurons above. new Neurons below*******");
		net.printNeurons();
	}
}

/*

    ( )           ( )
           ( )     
    ( )           ( )
           ( )
    ( )           ( )

A setup of {3,2,3} would result in a net that looks like this.
each neuron in each column of "( )" connected to each neuron in the next



*/
