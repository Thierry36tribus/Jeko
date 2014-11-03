'use strict'

window.Games = {}

Games.trim = function (s) {
    return s.replace(/^\s+/, '').replace(/\s+$/, '');
}

Games.randomInt = function(min, max) {
    return Math.round(Math.random() * (max - min) + min)
}

/* from http://bost.ocks.org/mike/shuffle/ */
Games.shuffle = function(array) {
  var m = array.length
  var t
  var i
  while (m) {
    i = Math.floor(Math.random() * m--)
    t = array[m]
    array[m] = array[i]
    array[i] = t
  }
  return array
}

/* MyConsQuizz ------------------------------------------------------- */

Games.MyConsQuizz = {}

Games.MyConsQuizz.init = function(gameDesc) {
    Games.MyConsQuizz.gameDesc = gameDesc
    Games.MyConsQuizz.reset()
}

Games.MyConsQuizz.reset = function() {
    Games.MyConsQuizz.questions = Games.MyConsQuizz.buildQuestions(Games.MyConsQuizz.gameDesc.data)
    Games.MyConsQuizz.questionIndex = -1
}

Games.MyConsQuizz.buildQuestions = function(saving) {
    var questions = []
    
    var randomFrom = function(cons, margin) {
        var result
        for (var i=0; i < 1000; i++) {
            result = Games.randomInt(cons/2,cons*2)
            if (result < cons - margin || result > cons + margin) {
                break
            }
        }
        //console.log('result for ' + cons + ', margin ' + margin + " = " + result)
        if (result > 100) {
            return Math.round(result/100)*100
        } else {
            return result
        }
    }
    
    var createQ1 = function(saving) {
        var cons = Math.round(saving.waterCons)
        var cons1 = randomFrom(cons,500)
        var cons2 = Games.randomInt(1,5)*100
        var q1 = {text: "La semaine dernière, ma consommation d'eau a été de :", answers : [{good: true,text : cons + " litres"},{text : cons1 + " litres"}, {text : cons2 + " litres"}]}
        Games.shuffle(q1.answers) 
        return q1
    }

    var createQ2 = function(saving) {
        // 1 kWh 100 kms source easycycle.ch
        var kms = Math.round(saving.electricityCons) * 100
        var kms1 = randomFrom(kms,1000)
        var kms2 = Games.randomInt(1,5)*100
        var q2 = {text: "Avec ma consommation d'électricité de cette semaine, en vélo à assistance électrique j'aurais pu parcourir : ", answers : [{good: true,text :  kms + " kms"},{text : kms1 + " kms"}, {text : kms2 + " kms"}]}
        Games.shuffle(q2.answers)        
        return q2
    }

    var createQ3 = function(saving,use) {
        var q3 = {text: "Par rapport à la semaine d'avant, ma consommation " + use.text + " a été :",answers: [{text: "Plus élevée"}, {text: "Moins élevée"},{text: "A peu près identique"}]}
        if (use.value < -0.1) {
            q3.answers[0].good = true
        } else if (use.value > 0.1) {
            q3.answers[1].good = true        
        } else {
            q3.answers[2].good = true                
        }
        Games.shuffle(q3.answers)            
        return q3
    }
    var createQ4 = function(saving) {
        var bottles = Math.round(saving.waterCons / 1.5)
        var bottles1 = randomFrom(bottles,400)
        var bottles2 = Games.randomInt(1,5)*100
        var q4 = {text: "La semaine dernière, j'ai consommé l'équivalent de :", answers : [{good: true,text : bottles + " bouteilles d'eau"},{text : bottles1 + " bouteilles d'eau"}, {text : bottles2 + " bouteilles d'eau"}]}
        Games.shuffle(q4.answers)         
        return q4
    }
    var createQ5 = function(saving,use) {
        var q5 = {text: "Par rapport à la moyenne des autres locataires, la semaine dernière ma consommation " + use.text + " a été :",answers: [{text: "Plus élevée"}, {text: "Moins élevée"},{text: "A peu près identique"}]}
        if (use.value * 1.1 > use.mean) {
            q5.answers[0].good = true
        } else if (use.value * 0.9 < use.mean) {
            q5.answers[1].good = true        
        } else {
            q5.answers[2].good = true                
        }
        Games.shuffle(q5.answers)    
        return q5
    }
    var createQ6 = function(saving) {
        // 1l de fioul =  10 kWh 
        var liters = saving.surface * Math.round(saving.heatingCons + saving.hotWaterCons) / 10
        var liters1 = randomFrom(liters,50)
        var liters2 = Games.randomInt(1,5)*10
        var q6 = {text: "Si j'utilisais une chaudière au fioul pour me chauffer, j'aurais consommé combien de litres cette semaine ?", answers : [{good: true,text :  liters + " litres"},{text : liters1 + " litres"}, {text : liters2 + " litres"}]}
        Games.shuffle(q6.answers)
        return q6
    }
    var createQ7 = function(saving) {
        // 1 stère - 1m3 1800 kWh, 40 buches par stère (buches de 50 x 20)
        var hCons = saving.heatingCons * saving.surface
        var wood = Math.round(75 * hCons / 1800)
        var wood1 = randomFrom(wood,50)
        var wood2 = Games.randomInt(1,5)*10
        var q7 = {text: "Si j'utilisais un poêle à bois pour me chauffer, j'aurais consommé combien de buches de bois (30 cm x 20 cm) cette semaine ?", answers : [{good: true,text :  wood + " buches"},{text : wood1 + " buches"}, {text : wood2 + " buches"}]}
        Games.shuffle(q7.answers)
        return q7
    }
    var createQ8 = function(saving) {
        // 1 kWh de gaz 0,238 kg de CO2
        var gasCons = (saving.heatingCons + saving.hotWaterCons) * saving.surface
        var co = Math.round(0.238 * gasCons)
        var co1 = randomFrom(co,50)
        var co2 = Games.randomInt(1,5)*10
        var q8 = {text: "Combien de kilos de CO2 ma consommation de gaz de cette semaine a t-elle dégagé ?", answers : [{good: true,text :  co + " kg"},{text : co1 + " kg"}, {text : co2 + " kg"}]}
        Games.shuffle(q8.answers)
        return q8
    }

    questions.push(createQ1(saving))    
    questions.push(createQ2(saving))
    
    var usesQ3 = [{text: "d'électricité", value : saving.electricity},{text: "d'eau", value : saving.water},{text: "d'eau chaude", value : saving.hotWater}]
    usesQ3.forEach(function(use){
        questions.push(createQ3(saving,use))        
    })
    questions.push(createQ4(saving))    
    
    var usesQ5 = [{text: "d'électricité", value : saving.electricityCons, mean : saving.electricityConsMean},{text: "d'eau", value : saving.waterCons, mean : saving.waterConsMean},{text: "d'eau chaude", value : saving.hotWaterCons, mean : saving.hotWaterConsMean}]
    if (saving.heatingCons > 0) {
        usesQ5.push({text: "de chauffage", value : saving.heatingCons, mean : saving.heatingConsMean})
    }
    usesQ5.forEach(function(use){
        questions.push(createQ5(saving,use))        
    })    
    if (saving.heatingCons > 0) {
        questions.push(createQ6(saving))
        questions.push(createQ7(saving))
    }
    questions.push(createQ8(saving))
    
    Games.shuffle(questions)
    return questions
}

function MyConsQuizz($scope,onGameEnded) {
    this.id = Games.MyConsQuizz.gameDesc.id
    this.title= Games.MyConsQuizz.gameDesc.title
    this.desc = Games.MyConsQuizz.gameDesc
    this.$scope = $scope
    this.onGameEnded = onGameEnded
}

MyConsQuizz.prototype.start = function() {
    Games.MyConsQuizz.questionIndex ++
    var that = this
    var sc = this.$scope
    
    sc.withoutTitle = matchMediaPhone()
    sc.$parent.withoutPoints = sc.withoutTitle
    
    sc.question = Games.MyConsQuizz.questions[Games.MyConsQuizz.questionIndex]

    var goodAnswer = function(question) {
        var text = 'Oups...'
        question.answers.forEach(function(answer){
            if (answer.good) {
                text = answer.text
            }
        })
        return text
    }
    
    sc.selectAnswer = function(answer) {
        if (sc.question.answered) return
        answer.selected = true
        sc.question.answered = true
        
        that.win = answer.good
        that.resultInfo = ''
        that.winText = 'Bonne réponse !'
        that.lostText = "La réponse correcte était : '" + goodAnswer(sc.question) + "'"
        that.ended = true
        that.onGameEnded()
    }

}

/* MyChart ------------------------------------------------------- */

Games.MyChart = {}

Games.MyChart.init = function(gameDesc) {
    Games.MyChart.gameDesc = gameDesc
    Games.MyChart.reset()
}

Games.MyChart.reset = function() {
    Games.MyChart.questions = Games.MyChart.buildQuestions(Games.MyChart.gameDesc.data)
    Games.MyChart.questionIndex = -1
}

Games.MyChart.buildDataChart = function(dwelling,consPropertyName) {
    var data = []
     dwelling.savings.forEach(function(saving){
        data.push({week: saving.week, value: saving[consPropertyName]})  
    })
    data.reverse()
    return data
}
Games.MyChart.buildQuestion = function(threeDwellings,consPropertyName,consHumanName) {
    var myData = this.buildDataChart(threeDwellings[0],consPropertyName)
    var data1 = this.buildDataChart(threeDwellings[1],consPropertyName)
    var data2 = this.buildDataChart(threeDwellings[2],consPropertyName)
    var question = {text: "Laquelle de ces courbes est celle de votre consommation " + consHumanName + " semaine après semaine ?", answers : [{good: true,data : myData},{data: data1}, {data:  data2}]}
    Games.shuffle(question.answers) 
    return question
}
Games.MyChart.buildQuestions = function(threeDwellings) {
    var questions = []
    questions.push(this.buildQuestion(threeDwellings,'waterCons',"d'eau")) 
    questions.push(this.buildQuestion(threeDwellings,'electricityCons',"d'électricité")) 
    questions.push(this.buildQuestion(threeDwellings,'hotWaterCons',"d'eau chaude")) 
    
    Games.shuffle(questions)
    return questions
}

function MyChart($scope,onGameEnded) {
    this.id = Games.MyChart.gameDesc.id
    this.title= Games.MyChart.gameDesc.title
    this.desc = Games.MyChart.gameDesc
    this.$scope = $scope
    this.onGameEnded = onGameEnded
}

MyChart.prototype.start = function() {
    Games.MyChart.questionIndex ++

    var that = this
    var sc = this.$scope
    
    sc.withoutTitle = matchMediaPhone()
    sc.$parent.withoutPoints = sc.withoutTitle
    
    sc.question = Games.MyChart.questions[Games.MyChart.questionIndex]

    var goodAnswer = function(question) {
        var goodIndex
        question.answers.forEach(function(answer,index){
            if (answer.good) {
                goodIndex = index + 1
            }
        })
        return goodIndex
    }
    
    sc.selectAnswer = function(answer) {
        if (sc.question.answered) return
        answer.selected = true
        sc.question.answered = true
        
        that.win = answer.good
        that.resultInfo = ''
        that.winText = 'Bonne réponse !'
        that.lostText = "La réponse correcte était : 'la courbe n° " + goodAnswer(sc.question) + "'"
        that.ended = true
        that.onGameEnded()
    }
}
/* JekoQuizz ------------------------------------------------------- */

Games.JekoQuizz = {}

Games.JekoQuizz.init = function(gameDesc) {
    Games.JekoQuizz.gameDesc = gameDesc
    Games.JekoQuizz.reset()
}

Games.JekoQuizz.reset = function() {
    Games.JekoQuizz.questions = Games.JekoQuizz.buildQuestions(Games.JekoQuizz.gameDesc.data)
    Games.JekoQuizz.questionIndex = -1
}

Games.JekoQuizz.buildQuestions = function(allTips) {
    var questions = []

    var createQuestion = function(tip) {
        var question = {text: tip.question, answers : [{good: true,text : tip.goodAnswer},{text: tip.answer2}, {text: tip.answer3},{text: tip.answer4}]}
        Games.shuffle(question.answers)                    
        return question
    }
    
    // on mélange et on prend les 6 premiers
    Games.shuffle(allTips)
    var question = []
    for (var i=0; i < 6; i++) {
        questions.push(createQuestion(allTips[i]))
    }
    
    Games.shuffle(questions)
    return questions
}

function JekoQuizz($scope,onGameEnded) {
    this.id = Games.JekoQuizz.gameDesc.id
    this.title= Games.JekoQuizz.gameDesc.title
    this.desc = Games.JekoQuizz.gameDesc
    this.$scope = $scope
    this.onGameEnded = onGameEnded
}

JekoQuizz.prototype.start = function() {
    Games.JekoQuizz.questionIndex ++
    var that = this
    var sc = this.$scope
    
    sc.withoutTitle = matchMediaPhone()
    sc.$parent.withoutPoints = sc.withoutTitle
    
    sc.question = Games.JekoQuizz.questions[Games.JekoQuizz.questionIndex]

    var goodAnswer = function(question) {
        var text = 'Oups...'
        question.answers.forEach(function(answer){
            if (answer.good) {
                text = answer.text
            }
        })
        return text
    }
    
    sc.selectAnswer = function(answer) {
        if (sc.question.answered) return
        answer.selected = true
        sc.question.answered = true
        
        that.win = answer.good
        that.resultInfo = ''
        that.winText = 'Bonne réponse !'
        that.lostText = "La réponse correcte était : '" + goodAnswer(sc.question) + "'"
        that.ended = true
        that.onGameEnded()
    }

}

