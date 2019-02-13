
public class Net_Runner
{
	public static void main (String[] args)
	{
		int b[] = {3,2,3};
		NeuralNet net = new NeuralNet(b);
		double c[] = {.45, .23, .55};
		System.out.println (net.forwardPropogate(c));
		net.printWeights();
		System.out.println ("\n************************");
		net.printBiases();
		System.out.println ("\n************************");
		net.printNeurons();
	}
}