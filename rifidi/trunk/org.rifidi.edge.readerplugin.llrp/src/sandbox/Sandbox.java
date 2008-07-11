package sandbox;

import org.llrp.ltk.generated.enumerations.ConnectionAttemptStatusType;

public class Sandbox {
	
	public Sandbox(){
		if (1==ConnectionAttemptStatusType.Failed_A_Reader_Initiated_Connection_Already_Exists){
			System.out.println("true");
		}else{
			System.out.println("false");
		}
	}

}
