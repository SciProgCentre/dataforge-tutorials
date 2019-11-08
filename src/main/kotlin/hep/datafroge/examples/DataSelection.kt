package hep.datafroge.examples

import hep.dataforge.data.data
import hep.dataforge.context.Context
import hep.dataforge.context.PluginFactory
import hep.dataforge.context.PluginTag
import hep.dataforge.data.first
import hep.dataforge.data.get
import hep.dataforge.data.static
import hep.dataforge.meta.Meta
import hep.dataforge.workspace.*
import kotlin.reflect.KClass

class DataSelectionPlugin: WorkspacePlugin(){


    val square = task<Int>("square") {
        model {
            // Use regex for data selection
            data("data.*")
        }
        map<Int> { data ->
            data * data
        }
    }

    val anotherSquare = task<Int>("anotherSquare") {
        model {
            //Get one item
            data(pattern = "anotherData\\[5\\]")
        }
        map<Int> { data ->
            data * data
         }
    }


    val groupSqure = task<Double>("group"){
        model {
            dependsOn(square)
        }
        reduceByGroup<Int> {
            env ->
            group("even", filter = { name, _ ->
                Regex("\\d{1,}").find(name.toString())!!.value.toInt() % 2 == 0 }) {
                result { data ->
                    env.context.logger.info { "Starting even" }
                    data.values.average()
                }
            }
            group("odd", filter = { name, _ -> Regex("\\d{1,}").find(name.toString())!!.value.toInt() % 2 == 1 }) {
                result { data ->
                    env.context.logger.info { "Starting odd" }
                    data.values.average()
                }
            }
        }
    }

    val delta = task<Double>("delta") {
        model {
            dependsOn(groupSqure)
        }
        reduce<Double> { data ->
            data["even"]!! - data["odd"]!!
        }
    }


    // Some boilerplate code
    override val tag: PluginTag = Companion.tag
    companion object : PluginFactory<DataSelectionPlugin> {

        override val type: KClass<out DataSelectionPlugin> = DataSelectionPlugin::class

        override fun invoke(meta: Meta, context: Context): DataSelectionPlugin =
                DataSelectionPlugin(meta)

        // Name of our plugin, this name be used for plugin search
        override val tag: PluginTag = PluginTag("Basic")
    }
}

fun main(){
    val workspace = Workspace {
        // Register our plugin in workspace
        context{
            plugin(DataSelectionPlugin())
        }

        data {
            repeat(10) {
                static("data[$it]", it)
                static("anotherData[$it]", -it)
            }
        }
    }
    // Run task from plugin use format "PluginName.TaskName"
    val result = workspace.run("Basic.delta")
    result.items.map{it.value.data?.get()}

    val anotherResult = workspace.run("Basic.anotherSquare")
    result.first()?.get()
}