/**
 * Created by tws on 07/04/2015.
 */

app.service('ServerService', function($http){

    //Method that calls the API to get the servers
    this.callServerListService = function(){

        console.log('callServerListService');
        var serviceUrl = 'http://localhost:8111/getServersFile';
        return $http({ method: 'GET', url: serviceUrl });

    };

    //Method that calls the API to update the servers file
    this.callUpdateServersService = function(dataToStore){

        console.log('callUpdateServersService');
        var serviceUrl = 'http://localhost:8111/updateServersFile/' + encodeURIComponent(dataToStore);
        return $http({ method: 'GET', url: serviceUrl });

    };


});
