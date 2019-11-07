package hep.datafroge.examples

import hep.dataforge.context.Context
import hep.dataforge.context.PluginFactory
import hep.dataforge.context.PluginTag
import hep.dataforge.data.first
import hep.dataforge.data.get
import hep.dataforge.data.static
import hep.dataforge.meta.*
import hep.dataforge.workspace.*
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import kotlin.reflect.KClass


enum class AproxType{
    linear,
    parabolla
}


class BasicMetaPlugin: WorkspacePlugin(){



    val linear = task<Int>("linear"){
        model{
            allData()
        }
        map<Int>{
            val a = meta["a"].int ?: 1
            val b = meta["b"].int ?: 0
            a*it + b
        }
    }

    val parabolla = task<Int>("parabolla"){
        model{
            allData()
        }
        map<Int>{
            val a = meta["a"].int ?: 1
            val b = meta["b"].int ?: 0
            val c = meta["c"].int ?: 0
            a*it*it + b*it + c
        }
    }


    val square = task<Int>("square") {
        model {
            val type = meta["approx_type"].enum<AproxType>() ?: AproxType.linear
            when (type) {
                AproxType.linear -> dependsOn(linear)
                AproxType.parabolla -> dependsOn(parabolla)
            }
        }
        map<Int> { data ->

            if (meta["print_messege"].boolean ?: false){
                context.logger.info { "Square says: \"My name is Pol Saimon\"" }
            }

            data * data
        }
    }
    // Some boilerplate code
    override val tag: PluginTag = Companion.tag
    companion object : PluginFactory<BasicMetaPlugin> {

        override val type: KClass<out BasicMetaPlugin> = BasicMetaPlugin::class

        override fun invoke(meta: Meta, context: Context): BasicMetaPlugin =
                BasicMetaPlugin(meta)

        // Name of our plugin, this name be used for plugin search
        override val tag: PluginTag = PluginTag("Basic")
    }
}

fun main(){
    val workspace = Workspace {
        // Register our plugin in workspace
        context{
            plugin(BasicMetaPlugin())
        }

        data {
            repeat(100) {
                static("myData[$it]", it)
            }
        }
    }

    // Run task with user meta
    val result = workspace.run("Basic.square"){
        "approx_type" put AproxType.linear
        "linear" put {
            "a" put 10
        }
        "print_messege" put true
    }

    val firstValue = result.first()?.get()


    // Build meta, for "y" will be used default value
    val meta = buildMeta{
        "vector" put {
            "x" put 10
            "z" put 20
        }
    }

    // Use user extension for meta
    println(meta["vector"].vector3d)
}


// User extension for Vector3D
val MetaItem<*>?.vector3d
    get() = this?.let{
        Vector3D(
            this.node["x"].double ?: 0.0,
            this.node["y"].double ?: 0.0,
            this.node["z"].double ?: 0.0
    )
    }