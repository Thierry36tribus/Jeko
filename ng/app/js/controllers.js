'use strict';

/* Fonctions communes à plusieurs controllers */

var debug = function(text) {
    var now = new Date()
    console.log(now.getMinutes() + ':' + now.getSeconds() + ':' + now.getMilliseconds() +  ' - ' + text)
}

/* Seuils de visite donnant droit à personnalisationd de l'arbre */
var CUSTOM_THRESHOLDS = [15,150,300]

if (typeof String.prototype.endsWith !== 'function') {
    String.prototype.endsWith = function(suffix) {
        return this.indexOf(suffix, this.length - suffix.length) !== -1;
    };
}

var onError = function(status,$location) {
    // un accès direct à la page sans être loggé provoque une erreur 500 (il cherche login.txt car requete json)
    if (status = 500) {
        // pour ne pas être redirigé vers l'url de la requête json
        $location.path("/")
        window.location = "/logout"
    }    
}

var getUiUser = function($scope,$http,$location,callback) {
    $scope.$parent.bodyClass="game"
     $http.get("/uiUser").success(function(user) {
        $scope.user = user
        $scope.user.isTenant = (user.role === 0)
        $scope.points = user.points
        $scope.$parent.pageLoaded = true
        if (callback) {
            callback()
        }
    }).error(function(data,status){
        onError(status,$location)
    })
}

var getOriginPointsEvent = function(origin) {
    var POINTS_TEXTS = {}
    POINTS_TEXTS['DECOR'] = 'Décor'
    POINTS_TEXTS['GIFT'] = 'Cadeau'
    POINTS_TEXTS['WIN_GAME'] = 'Jeko-Quizz'
    
    var text = POINTS_TEXTS[origin]
    if (!text) {
        text = '?'
    }
    return text
}

var managePoints = function($scope,$http,$location) {
    $http.get('/uiPointsEvents').success(function(events){
        $scope.pointsEvents = events
    }).error(function(data,status){
        onError(status,$location)
    })
    $scope.getOriginText = getOriginPointsEvent
}

var MN_HOURS_COEFF = 2387

var getMagicNumber = function() {
    var now = new Date()
    return now.getDay()*17589 + now.getHours()*MN_HOURS_COEFF
}

var isValidMagicNumber = function(magicNumber) {
    var mn = getMagicNumber()
    return mn - magicNumber <= MN_HOURS_COEFF
}


/* Vignettes shop */
var carrouselDecor = function($scope,$http,$interval) {    
    $http.get('/Application/shop').success(function(elements){
        $scope.decorsThumbnails = Games.shuffle(elements) 
    }).error(function(data,status){
        onError(status,$location)
    })
}

// uiSlider from http://prajwalkman.github.io/angular-slider/
angular.module('myApp.controllers', ['uiSlider'])
.controller('IndexCtrl', ['$scope','$http','$location','$window','$route', function($scope,$http,$location,$window,$route) {
    $scope.bodyClass = ''
    $http.post('/Use/visit?sw=' + window.screen.availWidth + '&sh=' + window.screen.availHeight + '&ww=' +  window.innerWidth + '&wh='+  window.innerHeight).success(function(infosArray){
        var uiUser = infosArray[0]
        $scope.userName = uiUser.name
        $scope.avatar = (uiUser.avatarName ? 'avatars/' + uiUser.avatarName : 'icon-account.png') 
        if (!uiUser.accepted) {
            $location.path('/account')    
        }
        
        TimeMachine.firstYear = infosArray[1]
        TimeMachine.firstWeek = infosArray[2]
        TimeMachine.lastWeekOfYear = infosArray[3]        
    }).error(function(data,status){
        onError(status,$location)
    })
    
    $scope.exit = function() {
        $location.path('/byebye')
    }
    angular.element($window).bind('orientationchange', function () {
        $route.reload()
    })
}])
.controller('InfosCtrl', ['$scope','$http','$location', function($scope,$http,$location) {
    $scope.$parent.withoutFooter = true
}])
.controller('ByeByeCtrl', ['$scope', function($scope) {
    
}])
.controller('AccountCtrl', ['$scope','$http','$location',function($scope,$http,$location) {  
    $scope.$parent.withoutFooter = false
    $scope.magicNumber = getMagicNumber()
    
    var originalUser
    var updateUser = function(user) {
        $scope.user = user
        $scope.user.isTenant = (user.role === 0)
        $scope.user.roleLabel = ['Locataire','Administrateur','Visiteur'][user.role]
        originalUser = angular.copy(user)        
        $scope.userModified = !user.accepted
    }
    $http.get("/uiUser").success(function(user) {
        updateUser(user)
        $scope.$parent.pageLoaded = true
    }).error(function(data,status){
        onError(status,$location)
    })
        
    $scope.cancel = function() {
        $scope.user = angular.copy(originalUser)
        $scope.userModified = false
    }
    
    $scope.save = function() {
        $http.post('/Application/saveUser',$scope.user).success(function(user) {
            if (!$scope.user.accepted) {
                // première fois
                $location.path('/all')   
            } else {
                updateUser(user)
                alert("Modifications enregistrées")
            }
            $scope.userModified = true
        }).error(function(){
            alert("Erreur lors de l'enregistrement")
        })
    }
    
    $scope.editingPassword = false
    $scope.editPassword = function() {
        $scope.editingPassword = true
    }
    $scope.savePassword = function() {
        if ($scope.pwd1 && $scope.pwd1.length > 0 && $scope.pwd1 === $scope.pwd2) {
            $http.post('/pwd?pwd=' + $scope.pwd1).success(function(){
                alert('Votre nouveau mot de passe a bien été enregistré')
                $scope.editingPassword = false

            }).error(function(){
                alert("Erreur, votre mot de passe n'a pas été enregistré")
            })
        } else {
            alert("Mot de passe incorrect")
        }
    }

    $scope.cancelPassword = function() {
        $scope.editingPassword = false
    }
    
    $scope.modified = function(){
        $scope.userModified = true
    }
}])
.controller('CustomCtrl', ['$scope','$http','$location',function($scope,$http,$location) {
    $scope.magicNumber = getMagicNumber()
    $scope.withoutPoints = true
    var tree
    $scope.custom = {}
    var onLoad = function() {
        if ($scope.user && Tree.symbolsLoaded) {
            $scope.customLevel = -1
            for (var threshold = CUSTOM_THRESHOLDS.length - 1; threshold >= 0; threshold--) {
                if ($scope.user.meVisits >= CUSTOM_THRESHOLDS[threshold]) {
                    $scope.customLevel = threshold
                    break;
                }
            }

            var isAllowed = function(customLevel,treeIndex) {
                return treeIndex == 0 || (treeIndex < 4 && customLevel >= 0) || (treeIndex < 7 && customLevel >= 1) || customLevel == 2
            }
            
            var symbols = []
            for (var i=0; i < Tree.TREE_SYMBOLS.length; i++) {
                var symbol = 'not-allowed.svg'
                if (isAllowed($scope.customLevel,i)) {
                    symbol = Tree.TREE_SYMBOLS[i] 
                }
                symbols.push({img:symbol,id:i})       
            }
            $scope.treeSymbols = symbols
            createTree($scope.user.avatarName)
        }
    }
    
    var createTree = function(avatarName) {
        $scope.custom.avatar = avatarName
        if ($scope.$parent.dwelling) {
            // on vient de 'mon arbre', on a les savings
            $scope.$parent.dwelling.avatar = avatarName
            tree = new Tree($scope.$parent.dwelling,false)
            tree.clear()
            if ($scope.custom.avatar == $scope.user.avatarName) {
                tree.initWithAllFruits()
                tree.draw()        
                tree.drawFruits()
            } else {
                var nbFruits = 0
                $scope.$parent.dwelling.savings.forEach(function(saving){
                    nbFruits += saving.elts.length
                })
                tree.draw()
                tree.drawRandomFruits('big-fruit.svg',Math.floor(nbFruits / 10))
                tree.drawRandomFruits('fruit.svg',nbFruits % 10)     
            }
        } else {
            tree = new Tree({avatar: avatarName},false)  
            tree.clear()
            tree.draw()
        }
    }
    
    getUiUser($scope,$http,$location,onLoad)

    Tree.setup(onLoad)
    
    $scope.selectTree = function(treeName) {
        if (treeName != 'not-allowed.svg') {
            $scope.modified = (treeName != $scope.user.avatarName)
            createTree(treeName)                
        }
    }

    $scope.cancel = function() {
        $scope.modified = false
        createTree($scope.user.avatarName)
    }
    $scope.save = function() {
        if ($scope.user.avatarName != $scope.custom.avatar) {
            $http.post('/Application/customize',$scope.custom).success(function() {
                $scope.modified = false
                $location.path('/me/' + $scope.user.dwellingId + '/' + getMagicNumber())
            }).error(function(){
                alert("Erreur lors de l'enregistrement")
            })
        }   
    }
}])
.controller('ShopCtrl', ['$scope','$http','$location',function($scope,$http,$location) {
    $scope.magicNumber = getMagicNumber()
    getUiUser($scope,$http,$location)
    managePoints($scope,$http,$location)    
    
    $scope.cart = {}
    $scope.cart.elements = []
    
    $scope.loadingData = true
    $http.get('/Application/shop').success(function(elements){
        $scope.elements = elements
        $scope.loadingData = false
    }).error(function(data,status){
        $scope.loadingData = false
        onError(status,$location)
    })
    
    var updateTotal = function() {
        $scope.cart.total = $scope.cart.elements.reduce(function(sum,element,index,array){
            return sum + element.price
        },0)
        $scope.canBuy = $scope.cart.elements.length > 0 && $scope.cart.total <= $scope.points        
    }
    
    $scope.addElement = function(element) {
        element.selected = !element.selected
        if (element.selected) {
            $scope.cart.elements.push(element)
        } else {
            $scope.removeElement(element)
        }
        updateTotal()
    }
    
    var removeElement = function(element) {
        for (var i =0; i < $scope.cart.elements.length; i++) {
            if ($scope.cart.elements[i].id == element.id) {
                $scope.cart.elements.splice(i,1);
                break;
            }
        }           
    }
    
    $scope.removeElement = function(element) {
        element.selected = false
        removeElement(element)
        updateTotal()
    }
    
    $scope.cancel = function() {
        $scope.cart = {}
        $scope.cart.elements = []
        $scope.canBuy = false
        $scope.elements.forEach(function(element){
            element.selected = false
        })
    }
    $scope.buy = function() {
        $http.post('/Application/buy',$scope.cart.elements).success(function() {
            $location.path('/all/')
        }).error(function(){
            alert("Erreur lors de l'ajout de décors")
        })
    }
    
    
}])
.controller('GamesCtrl', ['$scope','$http','$location','$routeParams','$document','$timeout',function($scope,$http,$location,$routeParams,$document,$timeout) {
    getUiUser($scope,$http,$location)    
    $scope.$parent.bodyClass=''
    $scope.$parent.withoutFooter = true
    $scope.firstTime = true
    $scope.goodAnswers = 0
    $scope.gameIndex = 0
        
    var onGameEnded = function() {
        $scope.goodAnswers += ($scope.game.win ? 1 : 0)
        if ($scope.goodAnswers == 3 || $scope.gameIndex == $scope.games.length-1) {
            $scope.ended = true
            $scope.started = false
        }

        $timeout(function() {
            var bottom = angular.element(document.getElementById('bottomAnchor'))
            $document.scrollToElement(bottom,0,1000)
        })
    }
        
    var buildGames = function() {
        var games = []
        
        for (var i=0; i < 10; i++) {
            games.push(new MyConsQuizz($scope,onGameEnded))
        }
        for (var i=0; i < 5; i++) {
            games.push(new JekoQuizz($scope,onGameEnded))
        }

        if (!matchMediaPhone()) {
            for (var i=0; i < 3; i++) {
                games.push(new MyChart($scope,onGameEnded))
            }
        }        
        
        Games.shuffle(games)
        return games
    }
    
    $scope.getTemplate = function() {
        return 'templates/game-' + $scope.game.id + '.html'
    }
    
    $scope.cancelGame = function() {
        $scope.nextGame()   
    }
    $scope.cancelAll = function(){
        $scope.ended = true
        $scope.started = false
    }
    
    $scope.nextGame = function() {
        $scope.gameIndex ++
        if ($scope.gameIndex < $scope.games.length) {
            $scope.game = $scope.games[$scope.gameIndex]
            $scope.game.start()
        } else {
            $scope.ended = true
            $scope.started = false
        }
    }
    
    $scope.goToMe = function() {
        MyConsQuizz.validated = true
        $location.path('/me/' + $scope.user.dwellingId + '/' + getMagicNumber())
    }
    
    $scope.loadingData = true
    $http.get('/Application/gameTypes').success(function(gameTypes){
        
        Games.JekoQuizz.init(gameTypes[0])
        Games.MyConsQuizz.init(gameTypes[1])
        Games.MyChart.init(gameTypes[2])
            
        $scope.startGame = function() {
            Games.JekoQuizz.reset()
            Games.MyConsQuizz.reset()
            Games.MyChart.reset()
            
            $scope.games = buildGames()
            $scope.firstTime = false
            $scope.started = true
            $scope.ended = false
            $scope.goodAnswers = 0
            $scope.gameIndex = 0
            $scope.game = $scope.games[$scope.gameIndex]
            $scope.game.start()
        }
        
        $scope.loadingData = false 
    }).error(function(data,status){
        $scope.loadingData = false
        onError(status,$location)
    })

    
}])
.controller('QuizzCtrl', ['$scope','$http','$location','$routeParams','$document','$timeout','$interval',function($scope,$http,$location,$routeParams,$document,$timeout,$interval) {
    $scope.magicNumber = getMagicNumber()
    getUiUser($scope,$http,$location)    
    managePoints($scope,$http,$location)  
    $scope.$parent.withoutFooter = true
    $scope.gameCount = 0
    $scope.goodAnswers = 0
    $scope.gameIndex = 0
        
    var onGameEnded = function() {
        var won = ($scope.game.win ? 1 : 0)
        $scope.goodAnswers += won
        $scope.points += won

        $timeout(function() {
            var bottom = angular.element(document.getElementById('bottomAnchor'))
            $document.scrollToElement(bottom,0,1000)
        })
    }
        
    var buildGames = function() {
        var games = []
        for (var i=0; i < 6; i++) {
            games.push(new JekoQuizz($scope,onGameEnded))
        }
        Games.shuffle(games)
        return games
    }
    
    $scope.getTemplate = function() {
        return 'templates/game-' + $scope.game.id + '.html'
    }
    
    $scope.cancelGame = function() {
        $scope.nextGame()   
    }
    $scope.cancelAll = function(){
        $scope.gameIndex = $scope.games.length-1
        $scope.nextGame()
    }
    
    $scope.nextGame = function() {
        $scope.gameIndex ++
        if ($scope.gameIndex == $scope.games.length) {
            $scope.ended = true
            $scope.started = false
            if ($scope.goodAnswers > 0) {
                $http.post('/Use/winGame?points=' + $scope.goodAnswers).success(function(pointsArray) {
                    $scope.points = pointsArray[0]
                    $scope.pointsEvents = pointsArray[1]
                    $scope.highestScore = pointsArray[2]
                })
            }            
        } else {
            $scope.game = $scope.games[$scope.gameIndex]
            $scope.game.start()
        }
    }
    
    $scope.loadingData = true
    $http.get('/Application/gameTypes').success(function(gameTypes){
        
        Games.JekoQuizz.init(gameTypes[0])
            
        $scope.startGame = function() {
            Games.JekoQuizz.reset()
            
            $scope.games = buildGames()
            $scope.gameCount ++
            $scope.started = true
            $scope.ended = false
            $scope.goodAnswers = 0
            $scope.gameIndex = 0
            $scope.game = $scope.games[$scope.gameIndex]
            $scope.game.start()
        }        
        $scope.loadingData = false 
    }).error(function(data,status){
        $scope.loadingData = false
        onError(status,$location)
    })

    carrouselDecor($scope,$http,$interval)
    
}])

