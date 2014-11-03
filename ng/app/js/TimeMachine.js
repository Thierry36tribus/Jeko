'use strict'

var PLAY_DELAY = 200

Date.prototype.addDays = function(days) {
    var dat = new Date(this.valueOf());
    dat.setDate(dat.getDate() + days);
    return dat;
}


TimeMachine.getDateOfWeekIndex = function (weekIndex) {
    // TimeMachine.firstWeek, TimeMachine.firstYear et TimeMachine.lastWeekOfYear sont initialisés par IndexCtrl (controller.js)
    
    var woy = TimeMachine.firstWeek + 1*weekIndex - 1
    var year = TimeMachine.firstYear
    if (woy > TimeMachine.lastWeekOfYear) {
        woy = woy - TimeMachine.lastWeekOfYear
        year ++
    }
    var date = TimeMachine.getDateOfWeek(year,woy)
    //console.log('getDateOfWeekIndex(' + weekIndex + ') : ' + year + ' - ' + woy + ', return ' + date)
    return date
} 

TimeMachine.getDateOfWeek = function (y, w) {
    var simple = new Date(y, 0, 1 + (w - 1) * 7);
    var dow = simple.getDay();
    var ISOweekStart = simple;
    if (dow <= 4)
        ISOweekStart.setDate(simple.getDate() - simple.getDay() + 1);
    else
        ISOweekStart.setDate(simple.getDate() + 8 - simple.getDay());
    return ISOweekStart;
}

function TimeMachine($scope,$interval,onWeekChanged,getNbWeeksToPlay) {   
    
    var timer
    
    var stopTimer = function() {
        $scope.tmPlaying = false
        if (angular.isDefined(timer)) {
            $interval.cancel(timer)
            timer = undefined
        }
    }

    $scope.$on('$destroy', function() {
        stopTimer()
    })

    $scope.previousWeek = function() {
        if ($scope.week > $scope.firstWeek) {
            $scope.week--
        }
    }
    $scope.nextWeek = function() {
        if ($scope.week < $scope.lastWeek) {
            $scope.week++
        }
    }
    $scope.play = function() {
        $scope.tmPlaying = ! $scope.tmPlaying
        if ($scope.tmPlaying) {
            if ($scope.week == $scope.lastWeek) {
                $scope.week = $scope.firstWeek
            }
            timer = $interval(function() {
                $scope.nextWeek()
                if ($scope.week == $scope.lastWeek) {
                    $scope.tmPlaying = false
                }
            },PLAY_DELAY,getNbWeeksToPlay())
        } else {
            if (timer) {
                stopTimer()
            }
        }
    }
    
    $scope.$watch('week', function(newWeek, oldWeek) {
        if (newWeek && newWeek != oldWeek) {
            $scope.firstWeekFromDate  = TimeMachine.getDateOfWeekIndex($scope.firstWeek)
            $scope.fromDate = TimeMachine.getDateOfWeekIndex(newWeek)
            $scope.toDate = $scope.fromDate.addDays(6)
            onWeekChanged()
        }
    })
    
}

TimeMachine.prototype.animate = function($scope,$interval) {
    if (!TimeMachine.doAnimation()) {
        return
    }
    $interval(function() {
        $scope.nextWeek()
    },PLAY_DELAY*5,1)
}

TimeMachine.doAnimation = function() {
    // désactivé car bof... il faudrait n'animer que les fruits ? Mais les verrait-on ? Oui si fade qque chose ?
    return false
    //return !matchMediaPhone()
}



