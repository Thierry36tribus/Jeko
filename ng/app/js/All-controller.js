'use strict';

// uiSlider from http://prajwalkman.github.io/angular-slider/
angular.module('myApp.All-controller', ['uiSlider'])
.controller('AllCtrl', ['$scope','$routeParams','$location','$http','$interval','$timeout','$localStorage','$window','$modal', function($scope,$routeParams,$location,$http,$interval,$timeout,$localStorage,$window,$modal) {
    $scope.$parent.withoutFooter = false
    $scope.$parent.bodyClass=''
    $scope.helpFirstIndex = 2

    $scope.bgList = 'url(img/backgrounds/bg-meadow.svg),url(img/backgrounds/bg-hill2.svg),url(img/backgrounds/bg-hill1.svg),url(img/backgrounds/bg-sky.svg)'

    $scope.weatherPopoverTrigger = matchMediaPhone() ? "click" : "mouseenter"
    
    $scope.loadingShapes = true
    Orchard.setup(function() {
        $timeout(function(){
            $scope.loadingShapes = false 
            onLoad()
        })
    })    
    
    var uiDwellings, orchard
    // on les initialise sinon le slider met le bazar...
    $scope.week = 1
    $scope.firstWeek = 1
    $scope.lastWeek = 2
    
    var percent = function(nb) {
        return 100 * nb / orchard.trees.length    
    }
    
    var weekChanged = function() {
        if (uiDwellings) {
            $scope.thisWeekNbFruits = 0
            var savingsNb = 0
            var savingsNbHeating = 0
            var savingsNbElectricity = 0
            var savingsNbWater = 0
            var savingsNbHotWater = 0
            orchard.trees.forEach(function(tree){
                tree.update($scope.week) 
                var saving = tree.getSaving($scope.week)
                if (saving.global > 0) {
                    savingsNb ++
                }
                if (saving.heating > 0) {
                    savingsNbHeating ++
                }
                if (saving.electricity > 0) {
                    savingsNbElectricity ++
                }
                if (saving.water > 0) {
                    savingsNbWater ++
                }
                if (saving.hotWater > 0) {
                    savingsNbHotWater ++
                }
                if (saving && saving.elts) {
                    $scope.thisWeekNbFruits += saving.elts.length
                }
            })
            $scope.savingsNbPercent = 0
            $scope.savingsNbEnergy = ''
            if (percent(savingsNb) >= 50) {
                $scope.savingsNbPercent = percent(savingsNb)
            } else {
                var max = Math.max(savingsNbHeating,savingsNbElectricity,savingsNbWater,savingsNbHotWater)
                $scope.savingsNbPercent = percent(max)
                if (max == savingsNbHeating) {
                    $scope.savingsNbEnergy = 'de chauffage'
                } else if (max == savingsNbElectricity) {
                    $scope.savingsNbEnergy = "d'électricité"
                } else if (max == savingsNbWater) {
                    $scope.savingsNbEnergy = "d'eau"
                } else if (max == savingsNbHotWater) {
                    $scope.savingsNbEnergy = "d'eau chaude sanitaire"
                }
            } 
            orchard.drawFruits()
        }
    }    
    
    var timeMachine = new TimeMachine($scope,$interval,weekChanged,function() {
        return orchard.trees[0].dwelling.savings.length
    })
    
    $scope.mouseMove = function(e) {
        if (matchMediaPhone()) {
            return
        }
        var x = e.pageX - 20
        var y = e.pageY - 40

        if ($scope.tooltip && orchard) {
            if (y < 200) {
                // pour empêcher son affichage intempestif au dessus des arbres, parfois...
                $scope.tooltip = false
            }
            angular.element("#tooltip").css('left',x)
            angular.element("#tooltip").css('top', y)    
        }
    }
    
    var validated = function() {
        return MyConsQuizz.validated
    }
    
    $scope.showMine = function(tooltip) {
        if (tooltip.mine) {
            if (validated()) {
                $location.path('/me/' + tooltip.dwellingId + '/' + getMagicNumber())                           
            } else {
                $location.path('/games')
            }
        }
    }
    
    var createOrchard = function(dwellings) {
        var trees =[]
        var posIndex = 0
        dwellings.forEach(function(dwelling){
            var tree = new Tree(dwelling,true)
            tree.showTooltip = function() {
                $timeout(function(){
                    $scope.tooltip = {fruits: tree.nbFruits, mine: tree.mine, dwellingId:tree.dwelling.id, hasHighestScore: tree.dwelling.hasHighestScore}
                },100)
            }
            tree.hideTooltip = function() {
                $timeout(function(){
                    $scope.tooltip = false
                })
            }
            var mine = (dwelling.id == dwellingId && dwelling.canSee)
            var pos
            if (mine) {
                pos = Orchard.treesCenter
            } else {
                if (posIndex == Orchard.POSITIONS.length) {
                    // cas admin, pas de 'mine'
                    pos = Orchard.treesCenter   
                } else {
                    pos = Orchard.treesPositions[posIndex]
                }
                posIndex ++
            }
            tree.setPosition(pos.x,pos.y,mine)
            tree.onClick = function() {
                $timeout(function(){
                    if (validated()) {
                        $location.path('/me/' + tree.dwelling.id + '/' + getMagicNumber())                           
                    } else {
                        $location.path('/games')
                    }
                })
            }
            trees.push(tree)
        })
        orchard = new Orchard(trees)
    }
    
    
    var replaceAt = function(str,index, character) {
        return str.substr(0, index) + character + str.substr(index+character.length)
    }
    
    var onLoad = function() {
        if ($scope.loadingData || $scope.loadingShapes) {
            return
        }
        orchard.draw()
        weekChanged()
            
        timeMachine.animate($scope,$interval)
        $http.get('/Application/decor').success(function(elementsArray){
            var decors = []
            var bgList = ''
            elementsArray.forEach(function(element){
                if (element.background) {
                    bgList += 'url(img/backgrounds/bg-' + element.image + '),'
                } else {
                    decors.push(element)
                }
            })
            bgList = bgList.substring(0,bgList.length-1)
            if (bgList.length > 0) {
                $scope.bgList = bgList             
            }
            orchard.drawDecor(decors)
        })
    }
    
    var dwellingId = ($routeParams.dwellingId ? $routeParams.dwellingId : 0)
    $scope.loadingData = true
    $http.get("/uiDwellings/" + dwellingId ).success(function(dwellings) {
        if (!dwellingId) {
            dwellingId = dwellings[0].id
        }
        var firstDwelling = dwellings[0]
        $scope.lastWeek = firstDwelling.savings[0].week
        $scope.firstWeek = firstDwelling.savings[firstDwelling.savings.length - 1].week
        Tree.firstWeek = $scope.firstWeek
        $scope.week = (TimeMachine.doAnimation() ? $scope.lastWeek - 1 : $scope.lastWeek)
        uiDwellings = dwellings
        createOrchard(uiDwellings)
        $scope.gaugeValue = computeGauge(dwellings)
        $scope.loadingData = false
        $scope.$parent.pageLoaded = true
        onLoad()
    }).error(function(data,status){
        $scope.loadingData = false
        debug('Error when loading dwellings')
        onError(status,$location)
    })

    $scope.introOptions = Help.allIntroOptions()
    
    if (!matchMediaPhone()) {
        $timeout(function(){
            // l'auto start démarre top tôt (a priori), l'aide météo est mal positionnée, en démarrant ici ça marche
            Help.startFirstTime('all',$scope,$localStorage)
        },1000)        
    }
    
    /* Jauge --------------------------------------- */
    
    var sumConsumptions = function(dwellings) {
        var summedCons = []
        for (var i=0; i < dwellings[0].savings.length;i++) {
           summedCons[i] = 0 
        }
        var sumSurface = 0
        dwellings.forEach(function(dwelling){
            sumSurface += dwelling.surface
            var savings = dwelling.savings.slice()
            savings.reverse()
            savings.forEach(function(saving,index){
                var heating = (saving.heatingCons > 0 ? saving.heatingCons * dwelling.surface : 0)
                // Ce coeff de conversion litres -> kWh est aussi dans MovingAverageCalculator.java
                var water = saving.waterCons * 0.045
                var hotWater = saving.hotWaterCons * dwelling.surface
                summedCons[index] += heating + saving.electricityCons + water + hotWater
            })
            //console.log('tree ' + dwelling.id + ' : ' + angular.toJson(summedCons))
        })
        //console.log('avant cumul ' + angular.toJson(summedCons))
        // summedCons contient, pour chaque semaine, la somme des conso de tous les logements.
        // reste à cumuler 
        for (var i=1; i < summedCons.length; i++) {
            summedCons[i] += summedCons[i-1]
        }
        //console.log('cumulées, total : ' + angular.toJson(summedCons))

        // et à ramener au m2
        for (var i=0; i < summedCons.length; i++) {
            summedCons[i] = summedCons[i] / sumSurface
        }
        //console.log('cumulées, au m2 : ' + angular.toJson(summedCons))

        // TODO à supprimer, pour test
        //summedCons[summedCons.length-2] = 10
        //summedCons[summedCons.length-1] = 11

        return summedCons
    }
    
    var computeGauge = function(dwellings) {
        var sumArray = sumConsumptions(dwellings)
        var goal = 151
        var gaugePercent = sumArray[sumArray.length-1] * 100 / goal
        //console.log("gaugePercent = " + gaugePercent + ", value=" + sumArray[sumArray.length-1])
        return gaugePercent
    }
    
    var modalInstanceCtrl = function ($scope, $modalInstance) {
        $scope.close = function () {
            $modalInstance.close();
        }
    }
    
    $scope.openGauge = function() {
        $modal.open({
            templateUrl: 'templates/gaugeModalContent.html',
            controller: modalInstanceCtrl,
            scope: $scope,
            size: 'lg',
            backdrop: 'static'
        })                   
    }
    
    /* Météo --------------------------------------- */
    
    var WEATHERS = {}
    WEATHERS['clear-day'] = "Beau temps"
    WEATHERS['clear-night'] = "Beau temps"
    WEATHERS['rain'] = "Pluie"
    WEATHERS['snow'] = "Neige"
    WEATHERS['sleet'] = "Neige fondue"
    WEATHERS['wind'] = "Vent"
    WEATHERS['fog'] = "Brouillard"
    WEATHERS['cloudy'] = "Nuageux"
    WEATHERS['partly-cloudy-day'] = "Partiellement nuageux"
    WEATHERS['partly-cloudy-night'] = "Partiellement nuageux"
 
    var updateWeather = function() {
        $http.get('/weather').success(function(weather){
            $scope.weather = weather
            if (weather.current) {
                $scope.weather.title = WEATHERS[weather.current]
            }
        }).error(function(data,status){
            onError(status,$location)
        })
    }
    
    updateWeather()
    // toutes les 10 min
    $interval(updateWeather,600*1000)
        
    
}])
    