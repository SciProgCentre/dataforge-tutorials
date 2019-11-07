package hep.datafroge.examples

import hep.dataforge.context.Context
import hep.dataforge.context.PluginFactory
import hep.dataforge.context.PluginTag
import hep.dataforge.data.first
import hep.dataforge.data.get
import hep.dataforge.data.static
import hep.dataforge.meta.Meta
import hep.dataforge.workspace.*
import kotlin.reflect.KClass

class BasicPlugin: WorkspacePlugin(){


    val square = task<Int>("square") {
        model {
            allData()
        }
        map<Int> { data ->
            data * data
        }
    }

    val fourth = task<Int>("fourth") {
        model {
            dependsOn(square) // Get output of square task, as input in this task
        }
        map<Int> { data ->
            data * data
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

    // Some boilerplate code
    override val tag: PluginTag = Companion.tag
    companion object : PluginFactory<BasicPlugin> {

        override val type: KClass<out BasicPlugin> = BasicPlugin::class

        override fun invoke(meta: Meta, context: Context): BasicPlugin =
            BasicPlugin(meta)

        // Name of our pligun, this name be used for plugin search
        override val tag: PluginTag = PluginTag("Basic")
    }
}

fun main(){
    val workspace = Workspace {
        // Register our plugin in workspace
        context{
            plugin(BasicPlugin())
        }

        data {
            repeat(100) {
                static("myData[$it]", it)
            }
        }
    }
    // Run task from plugin use format "PluginName.TaskName"
    val result = workspace.run("Basic.quaver")
    val firstValue = result.first()?.get()
}