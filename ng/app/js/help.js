'use strict'

window.Help = {}

var helpOnHelp = {   
                element: '#help-help',
                position: 'left',
                intro: "Cette aide ne vous sera pas montrée lors de votre prochaine visite, mais vous pourrez la retrouver ici à tout moment."
            } 

Help.allIntroOptions = function(dwellingId) {
    return {
        steps:[{   
                position: 'floating',
                tooltipClass: 'help-welcome',
                intro: "Bienvenue dans JEKO, un jeu pour suivre vos consommations chaque semaine.<br>Pour chaque logement, un arbre fruitier. Votre logement, c’est l’arbre le plus lumineux.<br>Chaque semaine, les fruits qui vont pousser sur votre arbre et sur ceux de vos voisins montrent les économies qui ont été faites.<br> Pour savoir comment vous avez gagné des fruits, cliquez sur votre arbre.<br>Amusez-vous à décorer le verger grâce aux abeilles que vous pourrez gagner en jouant au Jeko-Quizz."
            },{   
                element: "#help-orchard",
                position: 'top',
                intro: "Ca, c'est le verger.<br>Chaque arbre représente un logement de votre voisinage. Le plus lumineux, c'est votre arbre !<br>Chaque nouveau fruit représente des économies faites au cours de la semaine. <br>Un gros fruit = 10 petits fruits.<br>Cliquez sur votre arbre et vous aurez plus d'informations sur l'évolution de vos consommations."
            },{   
                element: '#help-weather',
                 position: 'bottom',
                intro: "Ici, votre météo du jour !",
            },{   
                element: '#gaugeSmall',
                position: 'bottom',
                intro: "Un arbre bientôt planté à la Grotte Rolland, grâce aux économies de chacun ?<br>Cliquez ici pour en savoir plus."

            },{   
                element: '#help-time-machine',
                position: 'top',
                intro: "Là, vous pouvez revoir comment le verger a changé depuis le début du jeu."
            }, helpOnHelp
        ],
        showStepNumbers: false,
        scrollToElement: true,
        showBullets: false,
        exitOnOverlayClick: true,
        exitOnEsc:true,
        nextLabel: '<strong>Suivant</strong>',
        prevLabel: '<span style="color:green">Précédent</span>',
        skipLabel: 'Quitter',
        doneLabel: "C'est fini !"
    }
}

Help.meIntroOptions = function() {
    return {
        steps:[{   
                element: "#treeCanvas",
                position: 'bottom',
                intro: "Chaque semaine, je peux gagner de nouveaux fruits grâce à mes économies d’eau, de gaz et d’électricité."
            },{   
                element: '#help-cons',
                 position: 'left',
                intro: "L'évolution de mes consommations.",
            },{   
                element: '#help-time-machine',
                position: 'top',
                intro: "Comment mon arbre a changé depuis le début du jeu."
            },{   
                element: '#help-points',
                position: 'top',
                intro: "Jouez pour gagner des abeilles qui vous permettront d'ajouter des décors ou des personnages dans le verger !"
            },helpOnHelp
        ],
        showStepNumbers: false,
        scrollToElement: true,
        showBullets: false,
        exitOnOverlayClick: true,
        exitOnEsc:true,
        nextLabel: '<strong>Suivant</strong>',
        prevLabel: '<span style="color:green">Précédent</span>',
        skipLabel: 'Quitter',
        doneLabel: "C'est fini !"
    }
}

Help.startFirstTime = function(page,$scope,$localStorage) {
    if (page == 'all') {
        if (!$localStorage.helpAll) {
            $scope.startHelp()
        }
        $localStorage.helpAll = 'done'
    } else {
        if (!$localStorage.helpMe) {
            $scope.startHelp()
        }
        $localStorage.helpMe = 'done'        
    }
}
