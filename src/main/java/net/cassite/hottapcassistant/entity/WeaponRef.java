package net.cassite.hottapcassistant.entity;

import io.vproxy.base.util.coll.Tuple;
import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Matrix;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.weapon.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class WeaponRef {
    public static List<WeaponRef> all() {
        //noinspection unchecked
        Tuple<Integer, Supplier<Weapon>>[] ls = new Tuple[]{
            new Tuple<Integer, Supplier<Weapon>>(40, SongHuiWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(39, JueXiangWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(38, XiaoXiaoJuFengWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(37, YueXingChuanWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(36, PianGuangLingYuWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(20, LingGuangWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(1, GeLaiPuNiWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(2, LiuQuanCheXinWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(3, PoJunWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(4, QiMingXingWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(5, YingZhiWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(6, BingFengZhiShiWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(7, BaErMengKeWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(8, BuMieZhiYiWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(9, HongLianRenWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(10, FuHeGongWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(11, HeiYaMingLianWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(12, V2RongQuDunWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(13, FouJueLiFangWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(14, ChiYanZuoLunWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(15, SiPaKeWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(16, BurnSiYeShiZiWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(17, GasSiYeShiZiWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(18, PoXiaoWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(19, WanDaoWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(21, LingDuZhiZhenWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(22, ALaiYeShiWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(23, ChaoDianCiShuangXingWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(24, ChuDongZhongJiWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(25, DianCiRenWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(26, GeDouDaoWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(27, HuanHaiLunRenWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(28, JiLeiShuangRenWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(29, LeiTingZhanJiWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(30, MoBiWuSiWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(31, QiangWeiZhiFengWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(32, ShengHenQuanZhangWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(33, ShuangDongChangQiangWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(34, YeQueZhiYuWeapon::new),
            new Tuple<Integer, Supplier<Weapon>>(35, ZhongJieZheWeapon::new),
            // next is 41
        };
        var ret = new ArrayList<WeaponRef>();
        for (var pair : ls) {
            ret.add(new WeaponRef(pair._1, pair._2));
        }
        return ret;
    }

    public final int id;
    public final String name;
    public final Image image;
    public final Supplier<Weapon> weaponSupplier;
    public Supplier<Integer> starsSupplier = null;

    public WeaponRef(int id, Supplier<Weapon> weaponSupplier) {
        this.id = id;
        this.weaponSupplier = weaponSupplier;
        if (weaponSupplier != null) {
            var w = weaponSupplier.get();
            this.name = w.getName();
            this.image = w.getImage();
        } else {
            this.name = "";
            this.image = null;
        }
    }

    public Weapon make(Matrix[] matrix) {
        var w = weaponSupplier.get();
        w.init(getStars(), matrix);
        return w;
    }

    public int getStars() {
        if (starsSupplier == null) return 6;
        var stars = starsSupplier.get();
        if (stars == null) return 6;
        return stars;
    }

    @Override
    public String toString() {
        return "WeaponRef{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", stars=" + getStars() +
               '}';
    }
}
