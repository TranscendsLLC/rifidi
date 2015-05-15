/**
 * Created by Alejandro on 07/04/2015.
 */

app.service('AppService', function($http, CommonService){

    //Method that calls the API to get the applications
    this.callAppListService = function(host){

        console.log('callAppListService');
        var serviceUrl = host + '/apps';
        return $http({ method: 'GET', url: serviceUrl });

    };

    //Method that calls the API to stop application
    this.callStopAppService = function(host, appId){

        console.log('callStopAppService');
        console.log('appId');
        console.log(appId);
        var serviceUrl = host + '/stopapp/' + appId;
        return $http({ method: 'GET', url: serviceUrl });

    };

    //Method that calls the API to start application
    this.callStartAppService = function(host, appId){

        console.log('callStartAppService');
        console.log('appId');
        console.log(appId);
        var serviceUrl = host + '/startapp/' + appId;
        return $http({ method: 'GET', url: serviceUrl });

    };

    //Method that calls the API to get the readzones given appId
    this.callReadzoneListService = function(host, appId){

        console.log('callReadzoneListService');
        console.log('appId');
        console.log(appId);
        var serviceUrl = host + '/getReadZones/' + appId;
        return $http({ method: 'GET', url: serviceUrl });

    };

    // Method that takes the xml response from server and returns the applications
    // belonging to specific group
    this.getAppsFromReceivedData = function(data, groupName){

        var xmlData = CommonService.getXmlObjectFromXmlServerData(data);

        //get the xml response and extract the values to construct the app list where group matches
        var appsXmlVector = xmlData.getElementsByTagName("app");

        var appsToReturn = [];

        for (var index = 0; index < appsXmlVector.length; index++) {

            var id = appsXmlVector[index].getElementsByTagName("id")[0].childNodes[0].nodeValue;
            var number = appsXmlVector[index].getElementsByTagName("number")[0].childNodes[0].nodeValue;
            var status = appsXmlVector[index].getElementsByTagName("status")[0].childNodes[0].nodeValue;

            //As id comes in the form 'AppGroup:AppName' we need to split it into two different variables

            var localGroupName = id.split(":")[0];
            var appName = id.split(":")[1];

            if (localGroupName == groupName) {

                //Add the application to app list
                var appElement = {
                    "number": number,
                    "status": status,
                    "groupName": groupName,
                    "appName": appName
                };

                appsToReturn.push(appElement);

            }
        }

        return appsToReturn;

    };

    // Method that takes the xml response from server and returns the readzones
    this.getReadzonesFromReceivedData = function(data){

        var xmlData = CommonService.getXmlObjectFromXmlServerData(data);

        //get the xml response and extract the values to construct the readzone list
        var readzonesXmlVector = xmlData.getElementsByTagName("entry");

        var readzonesToReturn = [];

        for (var index = 0; index < readzonesXmlVector.length; index++) {

            var value = readzonesXmlVector[index].getElementsByTagName("value")[0].childNodes[0].nodeValue;

            //Add the readzone to readzone list
            var readzoneElement = {
                "readzone": value
            };

            readzonesToReturn.push(readzoneElement);
        }

        return readzonesToReturn;

    };

    // Method that takes the xml response from server and returns the application groups
    this.getAppGroupsFromReceivedData = function(data){

        var xmlData = CommonService.getXmlObjectFromXmlServerData(data);

        //get the xml response and extract the values to construct the app group list
        var appsXmlVector = xmlData.getElementsByTagName("app");

        var appGroupsToReturn = [];

        for (var index = 0; index < appsXmlVector.length; index++) {

            var id = appsXmlVector[index].getElementsByTagName("id")[0].childNodes[0].nodeValue;
            var number = appsXmlVector[index].getElementsByTagName("number")[0].childNodes[0].nodeValue;
            var status = appsXmlVector[index].getElementsByTagName("status")[0].childNodes[0].nodeValue;

            //As id comes in the form 'AppGroup:AppName' we need to split it into two different variables

            var groupName = id.split(":")[0];
            var appName = id.split(":")[1];
            //console.log('groupName: ' + groupName);
            //console.log('appGroupsToReturn-before:');
            //console.log(angular.copy(appGroupsToReturn));

            if ( !appGroupNameExistsInVector(groupName, appGroupsToReturn) ) {

                //console.log('!appGroupNameExistsInVector');

                //Add the application group to list
                var appGroup = {"groupName": groupName, "readzoneAppId": number};
                appGroupsToReturn.push(appGroup);

            } else {

                //console.log('appGroupNameExistsInVector');

            }
        }

        console.log('appGroupsToReturn-after loop:');
        console.log(appGroupsToReturn);
        return appGroupsToReturn;

    };

    // Method that takes the xml response from server and returns the applications associated with group name
    this.getAppsFromReceivedData = function(data, appGroupName){

        var xmlData = CommonService.getXmlObjectFromXmlServerData(data);

        //get the xml response and extract the values to construct the app list
        var appsXmlVector = xmlData.getElementsByTagName("app");

        var appsToReturn = [];

        for (var index = 0; index < appsXmlVector.length; index++) {

            var id = appsXmlVector[index].getElementsByTagName("id")[0].childNodes[0].nodeValue;
            var number = appsXmlVector[index].getElementsByTagName("number")[0].childNodes[0].nodeValue;
            var status = appsXmlVector[index].getElementsByTagName("status")[0].childNodes[0].nodeValue;

            //As id comes in the form 'AppGroup:AppName' we need to split it into two different variables

            var localGroupName = id.split(":")[0];
            var appName = id.split(":")[1];
            //console.log('groupName: ' + groupName);
            //console.log('appGroupsToReturn-before:');
            //console.log(angular.copy(appGroupsToReturn));

            if ( localGroupName == appGroupName ) {

                //Add the application to list
                var appElement = {"groupName": localGroupName, "appId": number, "status": status, "appName": appName};
                appsToReturn.push(appElement);

            } else {

                //console.log('appGroupNameExistsInVector');

            }
        }

        console.log('appsToReturn-after loop:');
        console.log(appsToReturn);
        return appsToReturn;

    };

    function appGroupNameExistsInVector(groupName, appGroupVector){

        var appGroupExists = false;
        appGroupVector.forEach(function (appGroup) {

            if ( !appGroupExists && appGroup.groupName == groupName ){

                appGroupExists = true;

            }

        });

        return appGroupExists;

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

    };

});
