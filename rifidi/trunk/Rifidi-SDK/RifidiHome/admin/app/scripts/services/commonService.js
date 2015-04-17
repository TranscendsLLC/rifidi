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

    /*
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
    */

    // Compares two objects to be ordered.
    // The object must have a 'elementName' property
    this.compareElements = function(a, b){

        if (a.elementName.toLowerCase() < b.elementName.toLowerCase() ){
            return -1;
        }
        if (a.elementName.toLowerCase() > b.elementName.toLowerCase() ){
            return 1;
        }
        return 0;
    };

    this.setReadzonePropertiesHelpText = function(properties){

        //Iterate the properties to put the help text
        properties.forEach(function (property) {

            if (property.key == 'readerID') {

                property.helpText = 'The Service ID of the reader defined in the rifidi.xml';

            } else if (property.key == 'antennas') {

                property.helpText = '(optional) Where you list the antennas. Blank means all antennas';

            } else if (property.key == 'tagPattern') {

                property.helpText = '(optional) Where you can filter tags based on a regular expression';

            } else if (property.key == 'matchPattern') {

                property.helpText = '(optional) Where you either match or exclude the filter pattern. '
                + 'If set to true, only use tags that match the tagPattern. If set to false, '
                + 'filter out tags that match the tagPattern';

            }

        });

        return properties;

    };






});
