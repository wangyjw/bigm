package ss.pku.utils.jltl2ba;

import java.io.FileNotFoundException;
import java.io.IOException;

import ss.pku.utils.jltl2ba.HighLevel.Callback;
import ss.pku.utils.jltl2ba.HighLevel.CallersSubstitute;
import ss.pku.utils.jltl2ba.HighLevel.CallersSubstitute.Bundle;
import ss.pku.utils.jltl2ba.HighLevel.CallersSubstitute.Guard;
import ss.pku.utils.jltl2ba.HighLevel.CallersSubstitute.NeverClaim;
import ss.pku.utils.jltl2ba.HighLevel.CallersSubstitute.State;
import ss.pku.utils.jltl2ba.HighLevel.CallersSubstitute.Transition;
import ss.pku.utils.jltl2ba.HighLevel.CallersSubstitute.Guard.Atom;
import ss.pku.utils.jltl2ba.HighLevel.CallersSubstitute.Guard.Conjunction;
import ss.pku.utils.jltl2ba.HighLevel.CallersSubstitute.Guard.Disjunction;
import ss.pku.utils.jltl2ba.HighLevel.CallersSubstitute.Guard.Negation;
import ss.pku.utils.jltl2ba.HighLevel.CallersSubstitute.Guard.Truthhood;
import ss.pku.utils.jltl2ba.HighLevel.CallersSubstitute.State.AcceptAll;
import ss.pku.utils.jltl2ba.HighLevel.CallersSubstitute.State.AcceptInit;
import ss.pku.utils.jltl2ba.HighLevel.CallersSubstitute.State.AcceptSj;
import ss.pku.utils.jltl2ba.HighLevel.CallersSubstitute.State.TiInit;
import ss.pku.utils.jltl2ba.HighLevel.CallersSubstitute.State.TiSj;

public class LTL2B_Demo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Callback cb = new Callback() {
			
			@Override
			public Transition newTransitionSubstitute(Object guardSubstitute, State targetStateSubstitute) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public TiSj newStateTiSjSubstitute(String image) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public TiInit newStateTiInitSubstitute(String image) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public AcceptSj newStateAcceptSjSubstitute(String image) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public AcceptInit newStateAcceptInitSubstitute(String image) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public AcceptAll newStateAcceptAllSubstitute(String image) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Bundle newSkippingBundleSubstitute(State sourceStateSubstitute) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Truthhood newGuardTruthhoodSubstitute() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Negation newGuardNegationSubstitute(Guard arg) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Disjunction newGuardDisjunctionSubstitute(Guard arg1, Guard arg2) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Conjunction newGuardConjunctionSubstitute(Guard arg1, Guard arg2) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Atom newGuardAtomSubstitute(Object inputAtom) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Bundle newDeadEndBundleSubstitute(State sourceStateSubstitute) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Bundle newBundleSubstitute(State sourceStateSubstitute) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		CallersSubstitute.NeverClaim csn = new NeverClaim() {
			
			@Override
			public boolean add(Bundle bundle) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		
		try {
//			HighLevel.exec("a && b", cb, csn);
			LowLevel.exec("a R b");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
