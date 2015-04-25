/**
 * Created by tws on 07/04/2015.
 */

app.service('ServerService', function($http, CommonService){

    var host = 'http://localhost:8111/';

    //Method that calls the API to get the servers
    this.callServerListService = function(){

        console.log('callServerListService');
        var serviceUrl = host + 'getServersFile';
        return $http({ method: 'GET', url: serviceUrl });

    };

    //Method that calls the API to get the UI properties
    this.callUIPropertiesService = function(){

        console.log('callUIPropertiesService');
        var serviceUrl = host + 'getUIPropertiesFile';
        return $http({ method: 'GET', url: serviceUrl });

    };

    //Method that calls the API to update the servers file
    this.callUpdateServersService = function(dataToStore){

        console.log('callUpdateServersService');
        var serviceUrl = host + 'updateServersFile/' + encodeURIComponent(dataToStore);
        return $http({ method: 'GET', url: serviceUrl });

    };

    //Method that calls the API to update the UI properties file
    this.callUpdateUIPropertiesService = function(dataToStore){

        console.log('callUpdateUIPropertiesService');
        var serviceUrl = host + 'updateUIPropertiesFile/' + encodeURIComponent(dataToStore);
        return $http({ method: 'GET', url: serviceUrl });

    };

    //Method that calls the API to make a server ping
    this.callPingServerService = function(protocol, ipAddress, port){

        console.log('callPingServerService');
        console.log('protocol');
        console.log(protocol);
        console.log('ipAddress');
        console.log(ipAddress);
        console.log('port');
        console.log(port);

        var serviceUrl = protocol + '://' + ipAddress + ':' + port + '/ping';
        return $http({ method: 'GET', url: serviceUrl });

    };

    // Method that takes the xml response from server and returns the ping timestamp
    this.getPingTimestampFromReceivedData = function(data){

        var xmlData = CommonService.getXmlObjectFromXmlServerData(data);

        //get the xml response and extract the ping timestamp value
        var timestampXmlVector = xmlData.getElementsByTagName("timestamp");

        var serverTimestamp = timestampXmlVector[0].childNodes[0].nodeValue;

        return serverTimestamp;

    };

    // Method that takes the xml response from server and returns success or failure on editing servers ui properties
    this.getSaveServersUIMessageFromReceivedData = function(data){

        var xmlData = CommonService.getXmlObjectFromXmlServerData(data);

        //get the xml response and extract the message
        var xmlVector = xmlData.getElementsByTagName("message");

        var message = xmlVector[0].childNodes[0].nodeValue;

        return message;

    };

});
