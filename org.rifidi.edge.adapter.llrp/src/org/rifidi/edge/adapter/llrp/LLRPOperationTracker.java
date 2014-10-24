package org.rifidi.edge.adapter.llrp;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.llrp.ltk.generated.enumerations.C1G2BlockEraseResultType;
import org.llrp.ltk.generated.enumerations.C1G2BlockWriteResultType;
import org.llrp.ltk.generated.enumerations.C1G2KillResultType;
import org.llrp.ltk.generated.enumerations.C1G2LockResultType;
import org.llrp.ltk.generated.enumerations.C1G2ReadResultType;
import org.llrp.ltk.generated.enumerations.C1G2WriteResultType;
import org.llrp.ltk.generated.interfaces.AccessCommandOpSpecResult;
import org.llrp.ltk.generated.parameters.C1G2BlockEraseOpSpecResult;
import org.llrp.ltk.generated.parameters.C1G2BlockWriteOpSpecResult;
import org.llrp.ltk.generated.parameters.C1G2KillOpSpecResult;
import org.llrp.ltk.generated.parameters.C1G2LockOpSpecResult;
import org.llrp.ltk.generated.parameters.C1G2ReadOpSpecResult;
import org.llrp.ltk.generated.parameters.C1G2WriteOpSpecResult;
import org.llrp.ltk.types.UnsignedShort;

public class LLRPOperationTracker 
		implements Serializable {
	
	/** Map to keep track of all operations and responses received **/
	private Map<UnsignedShort, LLRPOperationDto> operationMap;
	
	/** milliseconds to sleep this thread and wait for timeout **/
	long millisToSleep = 500;
	
	/** current time waited **/
	long currentTimeWaited = 0;
	
	//int numberOfLoops = (int) (getTimeout() / millisToSleep);
	
	/**
	 *
	 */
	public LLRPOperationTracker() {
		super();

		operationMap = new HashMap<UnsignedShort, LLRPOperationDto>();
	}
	
	public void addOperationDto(LLRPOperationDto llrpOperationDto){
		operationMap.put(llrpOperationDto.getOpSpecId(), llrpOperationDto);
	}

	

	/**
	 * @return the operationMap
	 */
	public Map<UnsignedShort, LLRPOperationDto> getOperationMap() {
		return operationMap;
	}

	/**
	 * @param operationMap the operationMap to set
	 */
	public void setOperationMap(Map<UnsignedShort, LLRPOperationDto> operationMap) {
		this.operationMap = operationMap;
	}
	
	/**
	 * Set the result received
	 * @param result the received result of operation 
	 */
	public void setResult(AccessCommandOpSpecResult result){
		
		if (result instanceof C1G2BlockEraseOpSpecResult) {

			C1G2BlockEraseOpSpecResult res = (C1G2BlockEraseOpSpecResult) result;
			System.out.println("\n1.res.getResult(): "
					+ res.getResult());
			System.out.println("res.getOpSpecID(): "
					+ res.getOpSpecID());

			// Assign the result to appropriate operation in tracker
			operationMap.get(res.getOpSpecID()).setResponseResult(res);

		} else if (result instanceof C1G2BlockWriteOpSpecResult) {

			C1G2BlockWriteOpSpecResult res = (C1G2BlockWriteOpSpecResult) result;
			System.out.println("\n2.res.getResult(): "
					+ res.getResult());
			System.out.println("res.getOpSpecID(): "
					+ res.getOpSpecID());

			// Assign the result to appropriate operation in tracker
			operationMap.get(res.getOpSpecID()).setResponseResult(res);

		} else if (result instanceof C1G2KillOpSpecResult) {

			C1G2KillOpSpecResult res = (C1G2KillOpSpecResult) result;
			System.out.println("\n3.res.getResult(): "
					+ res.getResult());
			System.out.println("res.getOpSpecID(): "
					+ res.getOpSpecID());

			// Assign the result to appropriate operation in tracker
			operationMap.get(res.getOpSpecID()).setResponseResult(res);

		} else if (result instanceof C1G2LockOpSpecResult) {

			C1G2LockOpSpecResult res = (C1G2LockOpSpecResult) result;
			System.out.println("\n4.res.getResult(): "
					+ res.getResult());
			System.out.println("res.getOpSpecID(): "
					+ res.getOpSpecID());

			// Assign the result to appropriate operation in tracker
			operationMap.get(res.getOpSpecID()).setResponseResult(res);

		} else if (result instanceof C1G2ReadOpSpecResult) {

			C1G2ReadOpSpecResult res = (C1G2ReadOpSpecResult) result;
			System.out.println("\n5.res.getResult(): "
					+ res.getResult());
			System.out.println("res.getOpSpecID(): "
					+ res.getOpSpecID());

			// Assign the result to appropriate operation in tracker
			operationMap.get(res.getOpSpecID()).setResponseResult(res);

		} else if (result instanceof C1G2WriteOpSpecResult) {

			C1G2WriteOpSpecResult res = (C1G2WriteOpSpecResult) result;
			System.out.println("\n6.res.getResult(): "
					+ res.getResult());
			System.out.println("res.getOpSpecID(): "
					+ res.getOpSpecID());

			// Assign the result to appropriate operation in tracker
			operationMap.get(res.getOpSpecID()).setResponseResult(res);

		}
		
		
	}
	
	/**
	 * Checks if all results are received
	 * @return true if all results are received, otherwise returns false
	 */
	public boolean areAllResultsReceived(){
		
		for(LLRPOperationDto llrpOperationDto : getOperationMap().values()){
			
			if (llrpOperationDto.getResponseResult() == null){
				return false;
			}
		}
		
		return true;
		
	}
	
	/**
	 * Checks if all results are received and are success
	 * @return true if all results are received and are success
	 */
	public boolean areAllResultsSuccessful(){
		
		if (!areAllResultsReceived()){
			return false;
		}
		
		for(LLRPOperationDto llrpOperationDto : getOperationMap().values()){
			
			if (llrpOperationDto.getResponseResult() instanceof C1G2BlockEraseOpSpecResult) {

				C1G2BlockEraseOpSpecResult res = (C1G2BlockEraseOpSpecResult) llrpOperationDto.getResponseResult();
				
				if (res.getResult().intValue() != C1G2BlockEraseResultType.Success){
					
					return false;
					
				}
				
			} else if (llrpOperationDto.getResponseResult() instanceof C1G2BlockWriteOpSpecResult) {

				C1G2BlockWriteOpSpecResult res = (C1G2BlockWriteOpSpecResult) llrpOperationDto.getResponseResult();

				if (res.getResult().intValue() != C1G2BlockWriteResultType.Success){
					
					return false;
					
				}

			} else if (llrpOperationDto.getResponseResult() instanceof C1G2KillOpSpecResult) {

				C1G2KillOpSpecResult res = (C1G2KillOpSpecResult) llrpOperationDto.getResponseResult();
				
				if (res.getResult().intValue() != C1G2KillResultType.Success){
					
					return false;
					
				}

			} else if (llrpOperationDto.getResponseResult() instanceof C1G2LockOpSpecResult) {

				C1G2LockOpSpecResult res = (C1G2LockOpSpecResult) llrpOperationDto.getResponseResult();

				if (res.getResult().intValue() != C1G2LockResultType.Success){
					
					return false;
					
				}

			} else if (llrpOperationDto.getResponseResult() instanceof C1G2ReadOpSpecResult) {

				C1G2ReadOpSpecResult res = (C1G2ReadOpSpecResult) llrpOperationDto.getResponseResult();

				if (res.getResult().intValue() != C1G2ReadResultType.Success){
					
					return false;
					
				}

			} else if (llrpOperationDto.getResponseResult() instanceof C1G2WriteOpSpecResult) {

				C1G2WriteOpSpecResult res = (C1G2WriteOpSpecResult) llrpOperationDto.getResponseResult();

				if (res.getResult().intValue() != C1G2WriteResultType.Success){
					
					return false;
					
				}

			}
			
		}
		
		return true;
		
	}
	
}
