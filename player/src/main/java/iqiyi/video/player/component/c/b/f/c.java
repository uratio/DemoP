////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by FernFlower decompiler)
////
//
//package iqiyi.video.player.component.c.b.f;
//
//import android.text.TextUtils;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.View.OnClickListener;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import androidx.lifecycle.ViewModelProvider;
//import com.airbnb.lottie.LottieAnimationView;
//import com.iqiyi.video.qyplayersdk.adapter.j;
//import com.iqiyi.video.qyplayersdk.util.v;
//import java.util.Iterator;
//import java.util.List;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//import org.iqiyi.video.player.vertical.b.d;
//import org.iqiyi.video.player.vertical.b.k;
//import org.iqiyi.video.player.vertical.bean.UserLive;
//import org.iqiyi.video.player.vertical.i.g;
//import org.iqiyi.video.request.bean.SubscribeFollow;
//import org.iqiyi.video.request.bean.UpLive;
//import org.iqiyi.video.request.bean.PassportUser.Data;
//import org.iqiyi.video.utils.at;
//import org.qiyi.basecore.eventbus.MessageEventBusManager;
//import org.qiyi.basecore.widget.QiyiDraweeView;
//import org.qiyi.video.module.qypage.exbean.QYHaoFollowingUserEvent;
//
//public class c implements OnClickListener, b {
//    a a;
//    ImageView b;
//    LottieAnimationView c;
//    d d;
//    private org.iqiyi.video.player.g.d e;
//    private ViewGroup f;
//    private ViewGroup g;
//    private QiyiDraweeView h;
//    private RelativeLayout i;
//    private boolean j = false;
//    private QiyiDraweeView k;
//    private QiyiDraweeView l;
//    private TextView m;
//
//    public c(org.iqiyi.video.player.g.d var1, ViewGroup var2, a var3) {
//        this.e = var1;
//        this.f = var2;
//        this.a = var3;
//        MessageEventBusManager.getInstance().register(this);
//        this.g = (ViewGroup)this.f.findViewById(2131373316);
//        this.h = (QiyiDraweeView)this.f.findViewById(2131362621);
//        this.b = (ImageView)this.f.findViewById(2131366665);
//        this.i = (RelativeLayout)this.f.findViewById(2131365551);
//        this.c = (LottieAnimationView)this.f.findViewById(2131365546);
//        this.k = (QiyiDraweeView)this.f.findViewById(2131367431);
//        this.l = (QiyiDraweeView)this.f.findViewById(2131367445);
//        this.m = (TextView)this.f.findViewById(2131376202);
//        this.h.setOnClickListener(this);
//        this.b.setOnClickListener(this);
//        this.m.setOnClickListener(this);
//        this.c.setImageAssetsFolder("images/");
//        this.c.setAnimation("sidebar_follow_press.json");
//        this.c.loop(false);
//        this.c.addAnimatorListener(new 1(this));
//        this.k.setImageURI("https://m.iqiyipic.com/app/ishortvideo/qysv_ugc_living_icon_out_circle.webp");
//        this.l.setImageURI("http://m.iqiyipic.com/app/ishortvideo/qysv_ugc_anchor_living_flash.webp");
//    }
//
//    private void d() {
//        if (this.a.a() && v.a(this.m)) {
//            String var1 = org.iqiyi.video.player.vertical.i.g.a(this.e);
//            at.b("ppc_play", "author_name", var1, org.iqiyi.video.player.vertical.i.g.b(this.e));
//            at.b("ppc_play", "author_name", com.iqiyi.video.qyplayersdk.adapter.j.n(), var1, org.iqiyi.video.player.vertical.i.g.b(this.e));
//        }
//
//    }
//
//    public final void a() {
//        this.j = false;
//    }
//
//    final void a(int var1, String var2) {
//        List var3 = (List)((org.iqiyi.video.player.vertical.j.a)(new ViewModelProvider(this.e.h(), org.iqiyi.video.player.vertical.j.d.a(this.e.d().getApplication()))).get(org.iqiyi.video.player.vertical.j.a.class)).c().getValue();
//        if (var3 != null) {
//            Iterator var4 = var3.iterator();
//
//            while(var4.hasNext()) {
//                SubscribeFollow var5 = ((k)var4.next()).h.b;
//                if (var5 != null && TextUtils.equals(var2, var5.targetId)) {
//                    var5.subscribeStatus = var1;
//                }
//            }
//
//        }
//    }
//
//    public final void a(d var1) {
//        this.d = var1;
//        Data var2;
//        if (var1 != null) {
//            var2 = var1.a;
//        } else {
//            var2 = null;
//        }
//
//        if (var2 != null) {
//            if (TextUtils.isEmpty(var2.avatar)) {
//                v.c(this.h);
//            } else {
//                v.d(this.g);
//                v.d(this.h);
//                this.h.setImageURI(var2.avatar);
//            }
//        } else {
//            v.b(this.g);
//        }
//
//        d var3 = this.d;
//        Data var4;
//        if (var3 != null) {
//            var4 = var3.a;
//        } else {
//            var4 = null;
//        }
//
//        if (var4 != null) {
//            label88: {
//                if (!TextUtils.isEmpty(var4.id)) {
//                    SubscribeFollow var12 = var3.b;
//                    if (var12 == null || this.a == null) {
//                        break label88;
//                    }
//
//                    if (var12.subscribeStatus == 0) {
//                        v.d(this.b);
//                        v.d(this.i);
//                    } else {
//                        this.c.setProgress(0.0F);
//                        v.c(this.i);
//                    }
//
//                    if (!this.a.b(var12.targetId)) {
//                        break label88;
//                    }
//                }
//
//                v.c(this.i);
//            }
//        }
//
//        d var5 = this.d;
//        UpLive var6;
//        if (var5 != null) {
//            var6 = var5.c;
//        } else {
//            var6 = null;
//        }
//
//        UserLive var7;
//        if (var5 != null) {
//            var7 = var5.d;
//        } else {
//            var7 = null;
//        }
//
//        if ((var6 == null || !TextUtils.equals(var6.getLiveStatus(), "LIVE_TYPE")) && var7 == null) {
//            this.l.setVisibility(8);
//            this.k.setVisibility(8);
//        } else {
//            this.l.setVisibility(0);
//            this.k.setVisibility(0);
//        }
//
//        d var8 = this.d;
//        Data var9;
//        if (var8 != null) {
//            var9 = var8.a;
//        } else {
//            var9 = null;
//        }
//
//        String var10 = null;
//        if (var9 != null) {
//            var10 = var9.name;
//        }
//
//        if (!TextUtils.isEmpty(var10)) {
//            this.m.setText(String.format("@%s", var10));
//            this.m.setVisibility(0);
//            this.d();
//        } else {
//            this.m.setVisibility(8);
//        }
//
//        a var11 = this.a;
//        if (var11 != null && (var11.e() || this.a.c())) {
//            v.b(this.g);
//        }
//
//    }
//
//    public final void a(boolean var1) {
//        if (var1) {
//            this.d();
//        }
//
//    }
//
//    public final void b() {
//        d var1 = this.d;
//        if (var1 != null) {
//            UpLive var2 = var1.c;
//            if (var2 != null && TextUtils.equals(var2.getLiveStatus(), "LIVE_TYPE") || var1.d != null) {
//                at.c("ppc_play", "live");
//            }
//        }
//
//    }
//
//    public final void b(boolean var1) {
//        if (var1) {
//            v.b(this.g);
//        } else {
//            d var2 = this.d;
//            Data var3;
//            if (var2 != null) {
//                var3 = var2.a;
//            } else {
//                var3 = null;
//            }
//
//            if (var3 != null && !TextUtils.isEmpty(var3.avatar)) {
//                v.d(this.g);
//            }
//
//        }
//    }
//
//    public final void c() {
//        MessageEventBusManager.getInstance().unregister(this);
//    }
//
//    @Subscribe(
//        threadMode = ThreadMode.MAIN
//    )
//    public void handleFollowStateMessageEvent(QYHaoFollowingUserEvent var1) {
//        d var2 = this.d;
//        if (var2 != null && var2.b != null && var1 != null && this.b != null) {
//            if (this.c == null) {
//                return;
//            }
//
//            SubscribeFollow var3 = var2.b;
//            if (TextUtils.equals(String.valueOf(var1.uid), var3.targetId) && !this.j) {
//                if (var1.isFollowed) {
//                    this.a(1, var3.targetId);
//                    v.c(this.b);
//                    return;
//                }
//
//                this.a(0, var3.targetId);
//                v.d(this.b);
//            }
//        }
//
//    }
//
//    public void onClick(View var1) {
//        if (var1 == this.h) {
//            a var8 = this.a;
//            if (var8 != null && var8.b()) {
//                this.a.g();
//            }
//
//        } else if (var1 == this.b) {
//            a var3 = this.a;
//            if (var3 != null && var3.b()) {
//                d var4 = this.d;
//                if (var4 != null) {
//                    Data var5 = var4.a;
//                    if (var5 != null) {
//                        String var6 = var5.id;
//                        if (!this.a.a(var6)) {
//                            LottieAnimationView var7 = this.c;
//                            if (var7 != null) {
//                                v.d(var7);
//                                this.c.playAnimation();
//                                this.a.h();
//                            }
//                        }
//                    }
//                }
//            }
//
//        } else {
//            if (var1 == this.m) {
//                a var2 = this.a;
//                if (var2 != null && var2.b()) {
//                    this.a.i();
//                }
//            }
//
//        }
//    }
//}
