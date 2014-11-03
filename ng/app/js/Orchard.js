'use strict'

Orchard.setup = function(onSetup) {
    paper.setup('orchardCanvas')
    
    var elt = angular.element("#orchardCanvas")
    var currentWidth = elt.width()
    var currentHeight = elt.height()
    Orchard.treesCenter = Orchard.POSITION_CENTER
    Orchard.treesPositions = Orchard.POSITIONS

    if (currentHeight > currentWidth && currentHeight < 400) {
        Orchard.xs = true
        Orchard.treesCenter = Orchard.XS_POSITION_CENTER
        Orchard.treesPositions = Orchard.XS_POSITIONS
        Orchard.Y_OFFSET = 0
    }
    Tree.preload(onSetup)
}

Orchard.Y_OFFSET = 20 // 50 // 100

Orchard.POSITION_CENTER = {x:540,y:190}
Orchard.XS_POSITION_CENTER = {x:250,y:150}
    
Orchard.POSITIONS = [
    {x:20, y:170}, 
    {x:80,y:60},
    {x:150,y:240},
    {x:210,y:100},
    {x:350,y:60},
    {x:360,y:200},
    {x:670,y:100},
    {x:690,y:270},
    {x:790,y:190},
    {x:870,y:75},
    {x:930,y:200},
//    {x:970,y:170},
    {x:1050,y:150}
]

Orchard.XS_POSITIONS = [
    {x: 0,y: 30}, 
    {x: 140,y: 0},
    {x: 340,y:40},
    {x: 500,y: 0},    
    {x:0,y:120},
    {x:490,y:120},
    {x:130,y:320},
    {x:0,y:280},
    {x:110,y:170},
    {x:330,y:320},
    {x:390,y:190},
    {x:500,y:340}
]

function Orchard(trees) {  
    // tri des arbres: ceux qui sont 'derrière' (y du bas du tronc petit) en premier, les autres ensuite
    trees.sort(Tree.compareBottom)
    this.trees = trees

    var that = this

    var elt = angular.element("#orchardCanvas")
    var currentWidth = elt.width()
    var currentHeight = elt.height()

    this.scale = function() {
        // dimensions du canvas pour échelle = 1
        var REF_W = 1220
        var REF_H = 600
        var wZoom = currentWidth/REF_W
        var hZoom = currentHeight/REF_H
        // les 2 valeurs sont quasiment les mêmes, de toutes façons, quand height=auto
        var zoom = Math.min(wZoom,hZoom)
        
        if (currentHeight > currentWidth && currentHeight < 400) {
            zoom = hZoom *0.7
        } 
        paper.view.zoom = zoom
                
        Orchard.adjust = new paper.Point(paper.project.activeLayer.position.x - paper.view.center.x,  paper.project.activeLayer.position.y - paper.view.center.y)
        paper.project.activeLayer.position.x = paper.view.center.x
        paper.project.activeLayer.position.y = paper.view.center.y + Orchard.Y_OFFSET        
    }
    var firstSaving = trees[0].dwelling.savings[trees[0].dwelling.savings.length - 1]
}

Orchard.prototype.draw = function() {
    var that = this
    paper.project.activeLayer.removeChildren()
    that.trees.forEach(function(tree){
        //console.log('drawing tree ' + tree.dwelling.id + ': ' + tree.getBottom())
        tree.draw()
    })    
    this.scale()
    paper.view.draw()
    this.drawn = true
}

Orchard.prototype.drawFruits = function() {
    if (!this.drawn) {
        return
    }
    var that = this
    that.trees.forEach(function(tree){
        tree.drawFruits()
    })
    paper.view.draw()
}

Orchard.prototype.drawElement = function(element) {
    var that = this
    if (element.image.endsWith('.svg')) {
        paper.project.importSVG('img/shop/' + element.image,function(item) {
            var x =  element.x 
            var y = element.y

           if (element.image == 'balloon.svg' && !Orchard.xs) {
                y = 50
            }   

            if (Orchard.xs) {
                x = x / 2
                y = y  * 1.2
            }
            x = x - Orchard.adjust.x
            y = y - Orchard.adjust.y


            item.position = new paper.Point(x,y)

            that.animateDecor(element,item)
        })
    }
}
Orchard.prototype.drawDecor = function(elements) {
    var that = this
    if (elements) {
        elements.forEach(function(element){
            that.drawElement(element)
        })
    }    
}

Orchard.prototype.animateDecor = function(element,item) {
    if (element.image.indexOf('snail') == 0) {
        item.onFrame = function(event) {
            if (event.count % 6 == 0) item.position.x = item.position.x + 1
            
            if (item.position.x > Orchard.treesPositions[Orchard.treesPositions.length-1].x) {
                // TODO calculer mieux la position max
                item.position.x = 0
            }
        }
    } else if (element.image == 'balloon.svg' && !Orchard.xs) {
        var originalScaling = item.scaling
        var originalPosition = item.position
        item.onFrame = function(event) {
            item.scale(0.99)
            item.position.x += 0.15
            item.position.y -= 1
            if (item.scaling.x < 0.1) {
                item.scaling = originalScaling
                item.position = originalPosition
            }
        }        
    }
    
}

