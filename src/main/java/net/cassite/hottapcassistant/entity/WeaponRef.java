package net.cassite.hottapcassistant.entity;

import javafx.scene.image.Image;
import kotlin.Pair;
import net.cassite.hottapcassistant.data.Matrix;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.weapon.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class WeaponRef {
    public static List<WeaponRef> all() {
        //noinspection unchecked
        Pair<Integer, Supplier<Weapon>>[] ls = new Pair[]{
            new Pair<Integer, Supplier<Weapon>>(36, PianGuangLingYuWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(20, LingGuangWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(1, GeLaiPuNiWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(2, LiuQuanCheXinWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(3, PoJunWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(4, QiMingXingWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(5, YingZhiWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(6, BingFengZhiShiWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(7, BaErMengKeWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(8, BuMieZhiYiWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(9, HongLianRenWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(10, FuHeGongWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(11, HeiYaMingLianWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(12, V2RongQuDunWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(13, FouJueLiFangWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(14, ChiYanZuoLunWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(15, SiPaKeWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(16, BurnSiYeShiZiWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(17, GasSiYeShiZiWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(18, PoXiaoWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(19, WanDaoWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(21, LingDuZhiZhenWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(22, ALaiYeShiWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(23, ChaoDianCiShuangXingWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(24, ChuDongZhongJiWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(25, DianCiRenWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(26, GeDouDaoWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(27, HuanHaiLunRenWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(28, JiLeiShuangRenWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(29, LeiTingZhanJiWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(30, MoBiWuSiWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(31, QiangWeiZhiFengWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(32, ShengHenQuanZhangWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(33, ShuangDongChangQiangWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(34, YeQueZhiYuWeapon::new),
            new Pair<Integer, Supplier<Weapon>>(35, ZhongJieZheWeapon::new),
            // next is 37
        };
        var ret = new ArrayList<WeaponRef>();
        for (var pair : ls) {
            ret.add(new WeaponRef(pair.component1(), pair.component2()));
        }
        return ret;
    }

    public final int id;
    public final String name;
    public final Image image;
    private final Supplier<Weapon> weaponSupplier;
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
