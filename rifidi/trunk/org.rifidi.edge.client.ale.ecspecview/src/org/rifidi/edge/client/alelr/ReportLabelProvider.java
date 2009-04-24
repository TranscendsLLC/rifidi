/**
 * 
 */
package org.rifidi.edge.client.alelr;

import org.eclipse.jface.viewers.LabelProvider;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReport;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReportGroup;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReportGroupListMember;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReports;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ReportLabelProvider extends LabelProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof ECReports) {
			return ((ECReports) element).getSpecName();
		}
		if (element instanceof ECReport) {
			return ((ECReport) element).getReportName();
		}
		if (element instanceof ECReportGroup) {
			if(((ECReportGroup) element).getGroupName()==null){
				return "defaultgroup";
			}
			return ((ECReportGroup) element).getGroupName();
		}
		if (element instanceof ECReportGroupListMember) {
			return ((ECReportGroupListMember) element).getEpc().getValue();
		}
		return super.getText(element);
	}

}
