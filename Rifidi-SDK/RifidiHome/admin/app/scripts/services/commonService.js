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

    }

});
