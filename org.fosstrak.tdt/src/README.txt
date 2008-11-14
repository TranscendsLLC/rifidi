
Author:

Mark G. Harrison (Auto-ID Labs, Cambridge, UK)	mark.harrison@cantab.net


Disclaimer:	

This software is a beta version made available on an as-is basis.  The
prototype EPC Tag Data Translation is still under active development
and no warranty is expressed or implied about its suitability,
accuracy or reliability for any purpose.  No liability is accepted by
the authors or Auto-ID Labs for any loss or damages arising from the
use of the translation tool or any errors and omissions contained
within the software.  The authors strive to make the tool as reliable
and useful as possible and welcome all feedback and notification about
deficiencies, errors or problems.

Please e-mail mark.harrison@cantab.net to provide feedback.



The two subdirectories 'schemes' and 'auxiliary' need to be copied to
a local directory.  The path name of the local directory must be
provided as the only string input parameter to the TDTEngine
constructor.

The jar file should be included in your
CLASSPATH.

'schemes' contains Tag Data Translation definition files in xml format
			for all coding schemes defined in EPCglobal
			Tag Data Standards v1.1 r1.27
			
'auxiliary' contains a copy of 'ManagerTranslation.xml' obtained from
			http://www.onsepc.com/ManagerTranslation.xml
			This is used for converting GS1 Company Prefix
			values into small integer Company Prefix Index
			values for use on 64-bit EPC tags
			
TDTFrontEnd.java contains an example of how to use the TDT package and 
can be run by executing the jar file.

