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

    val fourth = task<Int>("fourth") {
        model {
            //Get one item
            data(pattern = "anotherData\\[5\\]")
        }
        map<Int> { data ->
            data * data * data * data
         }
    }

    val quaver = task<Int>("quaver"){
        model {
            dependsOn(fourth) // Get output of square task, as input in this task
        }
        map<Int> { data ->
            data * data
        }
    }

    val sumOfFourth = task<Int>("sum"){
        model {
            dependsOn(square)
        }
        reduce<Int> {
            data ->
            context.logger.info { "Reduce says: \"I get a ${data::class} with data\""}
            data.values.sum()
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
    val result = workspace.run("Basic.fourth")
    result.items.map { it.value.data?.get() }
}