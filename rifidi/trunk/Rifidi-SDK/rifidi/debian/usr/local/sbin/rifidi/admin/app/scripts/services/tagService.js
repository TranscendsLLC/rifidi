/**
 * Created by Alejandro on 20/02/2016.
 */

app.service('TagService', function($http, CommonService){

    //Method that calls the API to get the tags
    this.callCurrentTagsService = function(host, readerId){

        console.log('callCurrentTagsService');
        var serviceUrl = host + '/currenttags/' + readerId;
        return $http({ method: 'GET', url: serviceUrl });

    };

    // Method that takes the xml response from server and returns the list of tags
    this.getTagsFromReceivedData = function(data){

        var xmlData = CommonService.getXmlObjectFromXmlServerData(data);

        //get the xml response and extract the values to construct the tag list
        var xmlVector = xmlData.getElementsByTagName("tag");

        var tagsToReturn = [];

        for (var index = 0; index < xmlVector.length; index++) {

            var tagId = xmlVector[index].getElementsByTagName("id")[0].childNodes[0].nodeValue;
            var antennaId = xmlVector[index].getElementsByTagName("antenna")[0].childNodes[0].nodeValue;

            //Add the tag to tag list
            var tagElement = {
                "id": tagId,
                "antenna": antennaId
            };

            tagsToReturn.push(tagElement);

        }

        return tagsToReturn;

    };

});
