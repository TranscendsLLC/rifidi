/**
 * Created by Alejandro on 07/04/2015.
 */

app.service('SensorService', function($http, CommonService){

    //Method that calls the API to get the sensors
    this.callSensorListService = function(host){

        console.log('callSensorListService');
        var serviceUrl = host + '/readers';
        return $http({ method: 'GET', url: serviceUrl });

    };

    // Method that takes the xml response from server and returns the list of sensors

    this.getSensorsFromReceivedData = function(data){

        var xmlData = CommonService.getXmlObjectFromXmlServerData(data);

        //get the xml response and extract the values to construct the sensor list
        var xmlVector = xmlData.getElementsByTagName("sensor");

        var sensorsToReturn = [];

        for (var index = 0; index < xmlVector.length; index++) {

            var id = xmlVector[index].getElementsByTagName("serviceID")[0].childNodes[0].nodeValue;
            var type = xmlVector[index].getElementsByTagName("factoryID")[0].childNodes[0].nodeValue;

            //Add the sensor to sensors list
            var sensorElement = {
                "id": id,
                "type": type
            };

            sensorsToReturn.push(sensorElement);


        }

        return sensorsToReturn;

    };

    // Method that extracts from url used to stop app, the app id parameter
    // It is expected the url has the form: "http://localhost:8111/stopapp/{appNumber}"

    this.extractAppIdFromStopAppUrl = function(url){

        //console.log('extractAppIdFromStopAppUrl');
        //console.log('extractAppIdFromStopAppUrl.url:');
        //console.log(url);

        //find the text 'stopapp/' and extract value after that string
        var word = 'stopapp/';
        var indexInicio = url.lastIndexOf(word) + word.length;
        var appId = url.substring(indexInicio, url.length);

        //console.log("extractAppIdFromStopAppUrl.appId:");
        //console.log(appId);

        return appId;

    }



});
