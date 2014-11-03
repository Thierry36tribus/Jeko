'use strict'

// uiSlider from http://prajwalkman.github.io/angular-slider/
angular.module('myApp.Me-controller', ['uiSlider'])
.controller('MeCtrl', ['$scope','$routeParams','$location','$http','$interval','$timeout','$localStorage','$modal', function($scope,$routeParams,$location,$http,$interval,$timeout,$localStorage,$modal) {
    $scope.$parent.bodyClass=''
    $scope.$parent.withoutFooter = false
    $scope.helpFirstIndex = 1
    
    $scope.chartsOpened = true
    
    $scope.loadingShapes = true
    Tree.setup(function() {
        $timeout(function(){
            $scope.loadingShapes = false 
            onLoad()
        })
    })    
    
    var tree

    $scope.week = 1
    $scope.firstWeek = 1
    $scope.lastWeek = 2
        
    var createChartDetailsData = function() {
        var heatingSum = 0
        var electricitySum = 0
        var waterSum = 0
        var hotWaterSum = 0
        var max = 0
        var chartHeating = [] 
        var chartElectricity = []
        var chartWater = []
        var chartWaterLiters = []
        var chartHotWater = []

        for (var w = $scope.firstWeek; w <= $scope.week; w++) {
            var saving =  tree.getSaving(w)
            
            var date = TimeMachine.getDateOfWeekIndex(w).addDays(6)

            var heating = Math.round(saving.heatingCons * $scope.dwelling.surface)
            chartHeating.push({week:saving.week, date: date, value:heating})
            heatingSum += heating

            var electricity = Math.round(saving.electricityCons)
            chartElectricity.push({week:saving.week, date: date, value:electricity})
            electricitySum += electricity
            
            var water = Math.round(saving.waterCons * 0.045)
            chartWater.push({week:saving.week, date: date,value: water})
            chartWaterLiters.push({week:saving.week, date: date,value: Math.round(saving.waterCons)})
            waterSum += saving.waterCons
            
            var hotWater = Math.round(saving.hotWaterCons * $scope.dwelling.surface)
            chartHotWater.push({week:saving.week, date: date,value:hotWater})
            hotWaterSum += hotWater
            
            var thisMax = Math.max(heating,electricity,water,hotWater)
            if (thisMax > max) {
                max = thisMax
            }
        }
        for (var w = $scope.week*1 + 1; w <= $scope.lastWeek; w++) {
            chartHeating.push({week: w, value: 'no value'})
            chartElectricity.push({week: w, value: 'no value'})
            chartWater.push({week: w, value: 'no value'})
            chartHotWater.push({week: w, value: 'no value'})
        }
        $scope.dwelling.chartHeating = chartHeating
        $scope.dwelling.chartElectricity = chartElectricity
        $scope.dwelling.chartWater = chartWater
        $scope.dwelling.chartWaterLiters = chartWaterLiters
        $scope.dwelling.chartHotWater = chartHotWater
        $scope.heatingSum = Math.round(heatingSum)
        $scope.electricitySum = Math.round(electricitySum)
        $scope.waterSum = Math.round(waterSum) 
        $scope.hotWaterSum = Math.round(hotWaterSum)
        
        $scope.axisMax = max
    }
        
    var weekChanged = function() {
        if (tree) {
            $scope.currentSaving = tree.getSaving($scope.week)
            tree.update($scope.week)
            $scope.thisWeekNbFruits = 0
            if ($scope.currentSaving && $scope.currentSaving.elts) {
                $scope.thisWeekNbFruits += $scope.currentSaving.elts.length
            }
            $scope.totalFruits = tree.nbFruits
            tree.drawFruits()
            createChartDetailsData()
        }
    }
        
    var timeMachine = new TimeMachine($scope,$interval,weekChanged,function() {
        return tree.dwelling.savings.length
    })
    
    var onLoad = function() {
        if ($scope.loadingData || $scope.loadingShapes) {
            return
        }
        tree.draw()
        weekChanged()
        timeMachine.animate($scope,$interval)
    }
    
    var magicNumber = $routeParams.magicNumber
    if (!isValidMagicNumber(magicNumber)) {
        $location.path('/games')
    }
    
    var dwellingId = ($routeParams.dwellingId ? $routeParams.dwellingId : 0)
    $scope.loadingData = true
    $http.get("/uiDwelling/" + dwellingId ).success(function(dwelling) {
        if (dwelling.id != dwellingId) {
            $location.path('/me/' + dwelling.id + '/' + magicNumber)      
        }
        $scope.dwelling = dwelling
        
        if ($scope.parent) {
            // on le conserve pour la page custom
            $scope.$parent.dwelling = dwelling
        }
        
        $scope.points = dwelling.points
        tree = new Tree(dwelling,false)

        $scope.lastWeek = dwelling.savings[0].week
        $scope.firstWeek = dwelling.savings[dwelling.savings.length - 1].week
        Tree.firstWeek = $scope.firstWeek
        $scope.week = (TimeMachine.doAnimation() ? $scope.lastWeek - 1 : $scope.lastWeek)
        $scope.loadingData = false
        $scope.$parent.pageLoaded = true
        onLoad()
    }).error(function(data,status){
        $scope.loadingData = false
        debug('Error when loading dwelling')
        onError(status,$location)
    })
    
    managePoints($scope,$http,$location)    
    
    $scope.introOptions = Help.meIntroOptions()
    
    if (!matchMediaPhone()) {
        $timeout(function(){
            // l'auto start démarre top tôt (a priori), l'aide météo est mal positionnée, en démarrant ici ça marche
            Help.startFirstTime('me',$scope,$localStorage)
        },1000)        
    }
    /* Vignettes shop ----------------------------------------------------- */

    carrouselDecor($scope,$http,$interval)
    
    /* Courbes détaillées ----------------------------------------------------- */
    
    var visited = false
    
    var modalInstanceCtrl = function ($scope, $modalInstance) {
        $scope.close = function () {
            $modalInstance.close();
        }
    }
    
    var openCharts = function() {
        $modal.open({
            templateUrl: 'templates/chartModalContent.html',
            controller: modalInstanceCtrl,
            scope: $scope,
            size: 'lg',
            backdrop: 'static'
        })           
    }
    
    var openModal = function() {
        var newThreshold = -1
        if (visited) {
            openCharts()
        } else {
            $http.post('/Use/charts').success(function(meVisits) {
                visited = true
                for (var threshold=0; threshold < CUSTOM_THRESHOLDS.length; threshold++) {
                    if (CUSTOM_THRESHOLDS[threshold] == meVisits) {
                        newThreshold = threshold
                        break
                    }
                }
                if (newThreshold >= 0) {
                    $location.path('/custom')
                } else {
                    openCharts()
                }
            }).error(function(){
                openCharts()  
            })
        }
    }
    
    $scope.showHeatingChart = function() {
        $scope.chartEnergy = "Gaz - chauffage"
        $scope.chartSum = $scope.heatingSum
        $scope.unit = "kWh"
        $scope.chartData = $scope.dwelling.chartHeating
        openModal()
    }
    $scope.showElectricityChart = function() {
        $scope.chartEnergy = "Electricité"
        $scope.chartSum = $scope.electricitySum
        $scope.unit = "kWh"
        $scope.chartData = $scope.dwelling.chartElectricity
        openModal()
    }
    $scope.showWaterChart = function() {
        $scope.chartEnergy = "Eau"
        var s = '' + Math.round($scope.waterSum / 100.0) / 10.0
        $scope.chartSum = s.replace('.',',')
        $scope.unit = "m3"        
        $scope.chartData = $scope.dwelling.chartWaterLiters
        openModal()
    }
    $scope.showHotWaterChart = function() {
        $scope.chartEnergy = "Gaz - eau chaude"
        $scope.chartSum = $scope.hotWaterSum
        $scope.unit = "kWh"
        $scope.chartData = $scope.dwelling.chartHotWater
        openModal()
    }
                
}])
