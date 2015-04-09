/**
 * Created by tws on 07/04/2015.
 */

app.service('CommonService', function($http){

    // Method that takes the xml data got from server and return the javascript xml object
    this.getXmlObjectFromXmlServerData = function(data){

        var xmlData;
        if (window.DOMParser) {
            var parser = new DOMParser();
            xmlData = parser.parseFromString(data, "text/xml");
        }
        else // Internet Explorer
        {
            xmlData = new ActiveXObject("Microsoft.XMLDOM");
            xmlData.async = false;
            xmlData.loadXML(data);
        }

        return xmlData;

    };


    // Method that extracts from xml server data the value associated to tagName

    this.getElementValue = function(data, tagName) {

        var xmlObject = this.getXmlObjectFromXmlServerData(data);

        var elementValue = xmlObject.getElementsByTagName(tagName)[0].childNodes[0].nodeValue;
        return elementValue;

    };

    // Compares two servers elements to be ordered
    this.compareServersElements = function(a, b){

        if (a.displayName < b.displayName ){
            return -1;
        }
        if (a.displayName > b.displayName ){
            return 1;
        }
        return 0;
    };

    // Compares two objects to be ordered.
    // The object must have a 'elementName' property
    this.compareElements = function(a, b){

        if (a.elementName < b.elementName ){
            return -1;
        }
        if (a.elementName > b.elementName ){
            return 1;
        }
        return 0;
    };






});
