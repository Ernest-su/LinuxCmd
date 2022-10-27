package ernest.linuxcmd

import androidx.multidex.MultiDexApplication
import ernest.linuxcmd.setting.AppSetting

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        AppSetting.init(this)
    }
}