'use strict'

Tree.symbols = {}
Tree.symbolsLoaded = false

/* Arbres dans l'ordre du niveau nécessaire pour y accéder */
Tree.TREE_SYMBOLS = ['default-tree.svg','peuplier.svg','sapin.svg','cypres.svg','chene.svg', 'olivier.svg','acacia.svg','pin-maritime.svg','pin-parasol.svg','pin-parasol-2.svg','cocotier.svg']

Tree.FRUIT_SYMBOLS =  ['fruit.svg','big-fruit.svg']
    
Tree.randomInt = function(min, max) {
    return Math.round(Math.random() * (max - min) + min)
}

Tree.setup = function(onSetup) {
    paper.setup('treeCanvas')
    Tree.preload(onSetup)
}

Tree.preload = function(onSetup) {
    if (Object.keys(Tree.symbols).length > 0) {
        // déja chargé
        if (onSetup) {
            onSetup()
        }
        return
    } 
        
    var allSymbols = Tree.FRUIT_SYMBOLS.concat(Tree.TREE_SYMBOLS)
    allSymbols.push('crown.svg')
    allSymbols.forEach(function(shape) {
        paper.project.importSVG('img/avatars/' + shape,function(item){
            Tree.symbols[shape] = new paper.Symbol(item)
            if (onSetup && Object.keys(Tree.symbols).length == allSymbols.length) {
                Tree.symbolsLoaded = true
                if (onSetup) {
                    onSetup()
                }
            }
        })
    })    
}

Tree.compareBottom = function(t1,t2) {
    if (t1.getBottom() < t2.getBottom()) {
        return -1
    } else if (t1.getBottom() > t2.getBottom()) {
        return 1
    }
    return 0
}

function Tree(dwelling,inOrchard) {
    this.dwelling = dwelling
    this.inOrchard = inOrchard
    this.treeSymbol = Tree.symbols[dwelling.avatar]
}

Tree.prototype.setPosition = function(x,y,mine) {
    this.x = x
    this.y = y
    this.mine = mine
}

/* y du bas du tronc */
Tree.prototype.getBottom = function() {
    return this.y + this.treeSymbol.definition.bounds.height/2
}

Tree.prototype.clear = function() {
    paper.project.activeLayer.removeChildren()
}

Tree.prototype.draw = function() {
    var that = this
    var offsetBottom = 0
    if (!that.inOrchard) {
        that.x = paper.view.center.x
        that.y = paper.view.center.y        
    }
    if (that.mine) {
        that.treeSymbol = that.treeSymbol.clone()
    }
    
    if (that.treeSymbol) {
        var pos = new paper.Point(that.x,that.y)        
        that.treeItem = that.treeSymbol.place(pos)
        if (that.inOrchard) {
            var topLeft = new paper.Point(that.x - that.treeSymbol.definition.bounds.width/2 ,that.y - that.treeSymbol.definition.bounds.height/2)
            // rectangle invisible, pour les evts souris qui ne marchent pas avec les PlacedSymbol ?!!!
            var mouseItem = new paper.Path.Rectangle(topLeft,that.treeSymbol.definition.bounds.size)
            mouseItem.fillColor = new paper.Color(0,0,0,0)
                                    
            mouseItem.onMouseEnter = function(event) {
                if (matchMediaPhone()) {
                    return
                }               
                if (that.showTooltip) {
                    that.showTooltip()
                }
                if (that.mine) {   
                    document.body.style.cursor = "pointer"                    
                }

            }
            mouseItem.onMouseLeave = function() {
                if (matchMediaPhone()) {
                    return
                }
                if (that.hideTooltip) {
                    that.hideTooltip()
                }
                if (that.mine) {   
                    document.body.style.cursor = "default"
                }
            }                

            if (that.mine) {   
                var theTreeShape = that.getTreeShape()
                //theTreeShape.fillColor = '#B9F81C'
               /* theTreeShape.fillColor ={
                    gradient: {
                        stops: [['#D7FC04'], ['#98E202']]
                    },
                    origin: new paper.Point(that.treeSymbol.definition.bounds.size.width/2,0),
                    destination: new paper.Point(that.treeSymbol.definition.bounds.size.width/2,that.treeSymbol.definition.bounds.size.heigth)
                }*/      
                                    
                that.treeItem.symbol.definition.shadowColor = '#FFFFFF'
                that.treeItem.symbol.definition.shadowBlur = 35
                // pour effet plus lumineux
                that.treeItem.clone()
                that.treeItem.clone()
                that.treeItem.clone()
                that.treeItem.clone()
                that.treeItem.clone()
                mouseItem.onClick = function() {
                    document.body.style.cursor = "default";
                    that.onClick()
                }
                /* Pulse */
                that.treeItem.onFrame = function(event){
                    var sin = Math.abs(Math.sin(event.time))
                    //theTreeShape.fillColor.lightness = sin/3 + 0.4
                    that.treeItem.symbol.definition.shadowBlur = sin * 15
                }      
            }
            /* affichage coordonnées pour debug */
            
            /*
            var text = new paper.PointText({
                point: pos,
                content: Math.round(pos.x) + ',' + Math.round(pos.y),
                fillColor: 'black',
                fontFamily: 'Courier New',
                fontSize: 20
            })
            */
            if (this.dwelling.hasHighestScore) {
                this.drawHighestScore()
            }

        } else {
            var elt = angular.element("#treeCanvas")
            var currentWidth = elt.width()
            var currentHeight = elt.height()

            var zoomWidth = currentWidth / (that.treeSymbol.definition.bounds.width + 20)
            var zoomHeight = currentHeight / (that.treeSymbol.definition.bounds.height + 20)
            var zoom = Math.min(zoomWidth,zoomHeight)

            paper.view.zoom = zoom
            Tree.adjust = new paper.Point(paper.project.activeLayer.position.x - paper.view.center.x,  paper.project.activeLayer.position.y - paper.view.center.y)
            paper.project.activeLayer.position = paper.view.center
            paper.view.draw()
        }
    }
    this.drawn = true
}

Tree.prototype.getSaving = function(week) {
    return Tree.getSavingOfWeek(week,Tree.firstWeek,this.dwelling.savings)
}

Tree.getSavingOfWeek = function(week,firstWeek,savings) {
    return savings[savings.length - (week - firstWeek) - 1]
}   

Tree.prototype.createFruits = function(unitFruits) {
    var that = this
    this.nbFruits = unitFruits.length
    this.treeFruits = [] 
    var nbBig = 0
    for (var i = 9; i < unitFruits.length; i+=10) {
        that.treeFruits.push({x: unitFruits[i].x, y:  unitFruits[i].y, shape: 'big-fruit.svg'})   
        nbBig++
    }
    for (var i = nbBig*10; i < unitFruits.length; i++) {
        that.treeFruits.push({x: unitFruits[i].x, y:  unitFruits[i].y, shape: 'fruit.svg'})            
    }    
}

Tree.prototype.update = function(week) {
    var that = this
    var unitFruits = []
    for (var w = Tree.firstWeek; w <= week; w++) {
        var saving = this.getSaving(w)
        if (saving.global >= 0) {
            saving.elts.forEach(function(fruit){
                unitFruits.push(fruit)                        
            })
        }
    }
    this.createFruits(unitFruits)
}

/** pour custom */
Tree.prototype.initWithAllFruits = function() {
    var that = this
    var unitFruits = []
    if (this.dwelling.savings) {
        // on parcourt dans le même sens que dans la page 'mon arbre'
        for (var i = this.dwelling.savings.length - 1; i--; i > 0) {
            var saving = this.dwelling.savings[i]
            if (saving.global >= 0) {
                saving.elts.forEach(function(fruit){
                    unitFruits.push(fruit)                        
                })
            }        
        }
    }
    this.createFruits(unitFruits)
}

Tree.prototype.getTreeShape = function() {
    return this.treeItem.symbol.definition.getItem({name:'TreeShape'})
}

/* Fruits ------------------------------------------------------------ */

Tree.prototype.drawFruits = function() {
    if (!this.drawn) {
        return
    }

    var that = this
    if (!that.placedFruits) {
        that.placedFruits = []
    } else {
        that.placedFruits.forEach(function(placedFruit){
            placedFruit.remove()
        })
    }
    if (that.treeFruits) {
        that.treeFruits.forEach(function(fruit,index){
            var fruitItem = that.drawFruit(fruit)
            if (fruitItem) {
                that.placedFruits.push(fruitItem)
            }
        })
    }
    paper.view.draw()
}

Tree.prototype.drawFruit = function(fruit) {
    var that = this
    var fruitSymbol = Tree.symbols[fruit.shape]
    if (fruitSymbol) {
        var x = that.x - that.treeSymbol.definition.bounds.width/2 + fruit.x + fruitSymbol.definition.bounds.width/2
        var y = that.y - that.treeSymbol.definition.bounds.height/2 + fruit.y + fruitSymbol.definition.bounds.height/2
        if (that.inOrchard) {
            x = x - Orchard.adjust.x
            y = y - Orchard.adjust.y + Orchard.Y_OFFSET 
        } else {
            x = x - Tree.adjust.x
            y = y - Tree.adjust.y           
        }
        var point = new paper.Point(x,y)
        var fruitItem = fruitSymbol.place(point)
        if (fruit.shape == 'big-fruit.svg') {
            fruitItem.scale(1.2)
        }
        return fruitItem
    } else {
        console.log('error, no Symbol for ' + fruit.shape)
    }
}

Tree.prototype.drawRandomFruits = function(fruitName,nbFruits) {
    var theTreeShape = this.getTreeShape()
    var fruits = []
    for (var i=0; i < nbFruits; i++) {        
        var fruit = {x: 0, y: 0, shape: fruitName}  
        var symbol = Tree.symbols[fruit.shape]
        for (var j=0; j <1000; j++) {
            var point = new paper.Point(0,0)
            point.x = Tree.randomInt(0,theTreeShape.bounds.width - symbol.definition.bounds.width/2)
            point.y = Tree.randomInt(0,theTreeShape.bounds.height - symbol.definition.bounds.height/2) 
            if (theTreeShape.contains(point)) {
                fruit.x = point.x - symbol.definition.bounds.width/2
                fruit.y = point.y - symbol.definition.bounds.height/2
                break
            }
        }        
        var fruitItem = this.drawFruit(fruit)
        fruits.push(fruitItem)
    }
    paper.view.draw()
    return fruits
}

Tree.prototype.drawHighestScore = function() {
    var crownSymbol = Tree.symbols['crown.svg']
    var x = this.x
    var y = this.y - this.treeSymbol.definition.bounds.height/2 - crownSymbol.definition.bounds.height/2
    var point = new paper.Point(x,y)
    crownSymbol.place(point)   
}