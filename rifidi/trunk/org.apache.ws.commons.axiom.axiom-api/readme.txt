The official org.apache.ws.commons.axiom.axiom-api bundle had a optional package level dependency
on org.apache.ws.commons.axiiom-axiom-impl.  Since the impl bundle obviously has dependencies on 
the api bundle, this created a cycle and eclipse would not export it.  I removed the package 
dependecny on the impl bundle and created this one. 

This bundle is required for cxf to run.