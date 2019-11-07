package hep.datafroge.examples

import hep.dataforge.data.data
import hep.dataforge.data.first
import hep.dataforge.data.get
import hep.dataforge.data.static
import hep.dataforge.meta.boolean
import hep.dataforge.meta.get
import hep.dataforge.workspace.*


fun main(){
    val workspace = Workspace{
        // Push data to workspace
        data {
            repeat(100) {
                static("myData[$it]", it)
            }
        }

        // Describe task
        val square = task<Int>("square") { // Task return Int item
            // Describe task model
            model {
                allData() // Get all data with suited type
            }
            // Describe action run in task
            // map is classical one to one action
            map<Int> { data -> // Task get Int item
                context.logger.info { "Starting square on $data" } // Logging using context
                data * data
            }
        }
    }

    // Lazy run of task --- executing only 1 and 2 stage and data not computation
    println("Start task square")
    val result = workspace.run("square")
    println("Executed only 1 and 2 stage")
    println("")
    println("Start computation of first data node")
    val firstValue = result.first()?.get() as Int
    println("Another data don't computation")
    println("")
    println("Start computation of another data node")
    result.items.map { it.value }.sumBy { it.data!!.get() as Int }
}