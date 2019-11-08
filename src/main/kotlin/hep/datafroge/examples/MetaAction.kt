package hep.datafroge.examples

import hep.dataforge.context.Context
import hep.dataforge.context.PluginFactory
import hep.dataforge.context.PluginTag
import hep.dataforge.data.first
import hep.dataforge.data.get
import hep.dataforge.data.static
import hep.dataforge.meta.Meta
import hep.dataforge.meta.buildMeta
import hep.dataforge.workspace.*
import kotlin.reflect.KClass

class MetaActionPlugin: WorkspacePlugin(){


    val square = task<Int>("square") {
        model {
            allData()
        }
        mapAction<Int> {
            meta = buildMeta {
                "my_meta" put "another"
            }
            result {
                data ->
                context.logger.info {meta }
                context.logger.info {actionMeta }
                data * data
            }
        }
    }

    val fourth = task<Int>("fourth") {
        model {
            dependsOn(square) // Get output of square task, as input in this task
        }
        map<Int> { data ->
            context.logger.info { meta }
            data * data
        }
    }


    // Some boilerplate code
    override val tag: PluginTag = Companion.tag
    companion object : PluginFactory<MetaActionPlugin> {

        override val type: KClass<out MetaActionPlugin> = MetaActionPlugin::class

        override fun invoke(meta: Meta, context: Context): MetaActionPlugin =
                MetaActionPlugin(meta)

        // Name of our plugin, this name be used for plugin search
        override val tag: PluginTag = PluginTag("Basic")
    }
}

fun main(){
    val workspace = Workspace {
        // Register our plugin in workspace
        context{
            plugin(MetaActionPlugin())
        }

        data {
            repeat(100) {
                static("myData[$it]", it){
                    "year" put 1992
                    "experiment" put "CMS"
                }
            }
        }
    }

    workspace.run("Basic.fourth").first()?.get()
    workspace.run("Basic.fourth"){
        "my_meta" put "value"
    }.first()?.get()




}