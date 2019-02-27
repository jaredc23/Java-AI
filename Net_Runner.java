
public class Net_Runner
{
	public static void main (String[] args)
	{
		int b[] = {3,2,3};
		NeuralNet net = new NeuralNet(b, true);
		double c[] = {.45, .22, .11};
		net.forwardPropogate(c);
		net.printNeurons();
		double d[] = {1, 0.0, 0.0};
		for(int i = 0; i < 100; i++)
		{
			net.teach(c,d);
		}
		
		System.out.println ("\n***Old Neurons above. new Neurons below*******");
		net.printNeurons();
	}
}