'use strict'

var getUsers = function(Restangular, $scope,tenantsOnly,callback) {
    var baseUsers = Restangular.all('users')
    baseUsers.getList().then(function(users) {
        var usersArray = users
        if (tenantsOnly) {
              usersArray = []
              users.forEach(function(user){
                  if (user.role === 0) {
                      usersArray.push(user)
                  }
              })
        }
        $scope.users = usersArray
        if (callback) {
            callback(usersArray)
        }
    })    
}

var checkAdmin = function($scope,$http,$location) {
    $scope.$parent.bodyClass=''
    $scope.isAdmin = false
    $http.get("/checkAdmin").success(function(infos) {
        if (infos && infos.length > 0) {
            $scope.isAdmin = true
        } else {
            $location.path("/")            
        }
    }).error(function(){
        $location.path("/")
    })
}

angular.module('myApp.admin.controllers', ['restangular'])
.controller('AdminCtrl', ['$scope','$location','Restangular','$http',function($scope,$location,Restangular,$http) {
    checkAdmin($scope,$http,$location)
    getUsers(Restangular,$scope,false)
    
    $scope.createUser = function() {
        $location.path('/newuser')    
    }

    var baseDwellings = Restangular.all('dwellings')
    baseDwellings.getList().then(function(dwellings) {
      $scope.dwellings = dwellings
    })

    $scope.createUser = function() {
        $location.path('/newuser')    
    }
    $scope.createDwelling = function() {
        $location.path('/newdwelling')    
    }

}])
.controller('UserCtrl', ['$scope','$location','Restangular','$routeParams','$http',function($scope,$location,Restangular,$routeParams,$http) {
    checkAdmin($scope,$http,$location)
    var userId = ($routeParams.userId ? $routeParams.userId : 0)
    $scope.roles = [{value:0,label:'Locataire'},{value:1,label:'Administrateur'},{value:2,label:'Visiteur'}]

    var oneUser = Restangular.one('users',userId)
    oneUser.get().then(function(user) {
        if (user && user != "null") {
            $scope.user = user
        } else {
            $location.path('/admin')                    
        }  
    })

    $scope.cancel = function() {
        $location.path('/admin')
    }

    $scope.save = function() {
        if (!$scope.user.name || $scope.user.name.length == 0 || !$scope.user.login || $scope.user.login.length == 0) {
            //alert("Le nom et l'adresse email doivent être renseignés")
            return
        }
        if (userId == 0) {
            $scope.user.post().then(function(user){
                if (user.id) {
                    $location.path('/admin')                                
                } else {
                    // création refusée
                    $scope.user.name = user.name
                    $scope.user.login = user.login
                    $scope.user.role = user.role
                    $scope.user.lastError = user.lastError
                }
            })
        } else {
            $scope.user.put().then(function(user){
                $location.path('/admin')        
            })
        }
    }      
    $scope.del = function() {
        if (confirm("Supprimer cet utilisateur ?")) {
            $scope.user.remove().then(function(user){
                if (user) {
                    alert(user.lastError)
                } else {
                    $location.path('/admin')
                }
            })
        }
    }
    
    $scope.sendNewUser = function(user) {
        $http.post('/sendNewUser/' + userId).success(function(){
            alert('Le mail a été envoyé')
        }).error(function(){
            alert("Erreur, le mail n'a pas été envoyé")
        })
    }
    
    $http.get('/userEvents/' + userId).success(function(events){
        $scope.events = events
    })
    $scope.getEventTypeAsString = function(event) {
        if (event.eventType == 1) {
            return 'connexion'
        }
        if (event.eventType == 2) {
            return 'déconnexion'
        }
        return '?'
    }    
    
    $http.get('/userVisits/' + userId).success(function(visitsArray){
        $scope.visitCount = visitsArray[0]
        $scope.visits = visitsArray[1]
    })
    
    // fonction définie dans controller.js
    $scope.getOriginText = getOriginPointsEvent

    $http.get('/userPoints/' + userId).success(function(pointsArray) {
        $scope.points = pointsArray[0]
        $scope.pointsEvents = pointsArray[1]
    })
    
    $scope.getDeviceInfo = function(userAgent) {
        var parser = new UAParser(userAgent);
        var result = parser.getResult()
        return result.browser.name + ' ' + result.browser.version + ', ' + (result.device.model == undefined ? '' : result.device.model + ', ') + result.os.name + ' ' + result.os.version
        
    }
    

}]) 
.controller('DwellingCtrl', ['$scope','$location','Restangular','$routeParams','$http',function($scope,$location,Restangular,$routeParams,$http) {
    checkAdmin($scope,$http,$location)

    var dwellingId = ($routeParams.dwellingId ? $routeParams.dwellingId : 0)

    var oneDwelling = Restangular.one('dwellings',dwellingId)
    oneDwelling.get().then(function(dwelling) {
        getUsers(Restangular,$scope,true,function(users){
            // pour faire correspondre le dwelling.user avec l'instance qui est dans la liste
            if (dwelling && dwelling.user) {
                for (var i=0; i < users.length; i++) {
                    if (dwelling.user.id == users[i].id) {
                        dwelling.user = users[i]
                        break
                    }
                }
            }
            
        })

        if (dwelling && dwelling != "null") {
            $scope.dwelling = dwelling
        } else {
            $location.path('/admin')                    
        }  
    })

    $scope.cancel = function() {
        $location.path('/admin')
    }

    $scope.save = function() {
        if (!$scope.dwelling.label || $scope.dwelling.label.length == 0) {
            return
        }
        if (dwellingId == 0) {
            $scope.dwelling.post().then(function(dwelling){
                $location.path('/admin')                                
            })
        } else {
            $scope.dwelling.put().then(function(dwelling){
                $location.path('/admin')        
            })
        }
    }      
    $scope.del = function() {
        if (confirm("Supprimer ce logement ?")) {
            $scope.dwelling.remove().then(function(dwelling){
                $location.path('/admin')
            })
        }
    }

}])  
.controller('ConfigCtrl', ['$scope','FileUploader','$location','$http',function($scope,FileUploader,$location,$http) {
    checkAdmin($scope,$http,$location)

    $http.get('/upload/consFiles').success(function(files){
        $scope.consFiles = files
    })
    
    /* upload files - https://github.com/nervgh/angular-file-upload */
    var uploader = $scope.uploader = new FileUploader({
        scope: $scope,                          // to automatically update the html. Default: $rootScope
        url: '/uploadSavings',
        autoUpload : true,
        /*removeAfterUpload: true,*/
        formData: [
            { key: 'value' }
        ],
        onSuccessItem: function(item, response) {
            $scope.consFile = response[0]
            $scope.consFiles.unshift($scope.consFile)
            $scope.rawSavings = response[1]
            $scope.consImported = false
        },
        onBeforeUploadItem: function() {
            $scope.processing = true
        },
        onErrorItem: function() {
            alert("Erreur, vérifiez le format du fichier")
        },
        onCompleteItem: function() {
            $scope.processing = false
        }
    })
    
    $scope.importCons = function() {
        $scope.processing = true
        $http.post('/importSavings/' + $scope.consFile.id).success(function(){
            $scope.consImported = true
            $scope.consFile.imported = true
            $scope.processing = false
        }).error(function(){
            $scope.processing = false
            alert('Erreur lors du traitement du fichier')
        })        
    }
    
    $scope.notifyUsers = function() {
        if (confirm("Confirmez-vous l'envoi des courriels et SMS ?")) {
            $http.post('/upload/notifyUsers',{year: $scope.consFile.year, week:$scope.consFile.week}).success(function(){
                $scope.consImported = false
                $scope.consFile = undefined
                uploader.clearQueue()
            }).error(function(){
                alert('Erreur lors de la notification des utilisateurs')
            })        
        }
    }

    /* Conseils  --------------------- */
    
    $http.get('/upload/tipsFiles').success(function(files){
        $scope.tipsFiles = files
    })
    
    var tipsUploader = $scope.tipsUploader = new FileUploader({
        scope: $scope,                         
        url: '/uploadTips',
        autoUpload : true,
        /*removeAfterUpload: true,*/
        formData: [
            { key: 'value' }
        ],
        onSuccessItem: function(item, response) {
            $scope.tipsFile = response[0]
            $scope.tipsFiles.unshift($scope.tipsFile)
            $scope.tips = response[1]
            $scope.tipsImported = false
        },
        onBeforeUploadItem: function() {
            $scope.processing = true
        },
        onErrorItem: function() {
            alert("Erreur, vérifiez le format du fichier")
        },
        onCompleteItem: function() {
            $scope.processing = false
        }        
    })
    
    $scope.importTips = function() {
        $scope.processing = true
        $http.post('/importTips/' + $scope.tipsFile.id).success(function(){
            $scope.tipsFile.imported = true
            $scope.processing = false
            $scope.tipsFile = undefined
            tipsUploader.clearQueue()
        }).error(function(){
            $scope.processing = false
            alert('Erreur lors du traitement du fichier')
        })        
    }
    
    
}])
.controller('ChartsCtrl', ['$scope','$http','$location','$timeout',function($scope,$http,$location,$timeout) {
    checkAdmin($scope,$http,$location)
    
    var createChartData = function(dwelling) {
        var chartData = []
        for (var w = $scope.firstWeek+1; w <= $scope.lastWeek; w++) {
            var saving =  Tree.getSavingOfWeek(w,$scope.firstWeek,dwelling.savings)
            chartData.push({
                week:saving.week, 
                global:saving.global*100, 
                heatingCons:saving.heatingCons,
                electricityCons:saving.electricityCons/10,
                waterCons:saving.waterCons/500,
                hotWaterCons:saving.hotWaterCons*5,
                fruits: saving.elts.length,
                
            })
        }
        return chartData
    }    
        
    $scope.loadingData = true
    $http.get("/uiDwellings/0?details=true").success(function(dwellings) {
        $scope.dwellings = dwellings                   
        $scope.firstWeek = dwellings[0].savings[dwellings[0].savings.length - 1].week
        $scope.lastWeek = dwellings[0].savings[0].week
         
        dwellings.forEach(function(dwelling){
            dwelling.chartData = createChartData(dwelling)
        })
                           
        $scope.loadingData = false
    }).error(function(){
        $scope.loadingData = false
        debug('Error when loading dwellings')
    })
          
}])

