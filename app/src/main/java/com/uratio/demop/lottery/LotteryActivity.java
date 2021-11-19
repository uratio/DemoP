package com.uratio.demop.lottery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.uratio.demop.R;

import java.util.ArrayList;
import java.util.List;

public class LotteryActivity extends AppCompatActivity {
    private LotteryView nl;
    private TextView tvIntegral;

    private String[] arrDescs = {"20元现金优\n惠券", "价值588元\n京佳口腔洗牙卡", "100元现金\n优惠券", "途牛旅游1663元\n组合出行优惠券", "", "星美影院9元\n通用立减券", "50元现金优\n惠券",
            "幸福西饼30元\n优惠券", "免服务费权益"};

    int[] arrImgs = {R.drawable.icon_lottery_prize_1, R.drawable.icon_lottery_prize_2, R.drawable.icon_lottery_prize_3, R.drawable
            .icon_lottery_prize_4, R.drawable.icon_lottery_start, R.drawable.icon_lottery_prize_6, R.drawable.icon_lottery_prize_7, R.drawable
            .icon_lottery_prize_8, R.drawable.icon_lottery_prize_9};

    private int integral = 50;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery);

        nl = (LotteryView) findViewById(R.id.nl);
        tvIntegral = (TextView) findViewById(R.id.tv_integral);
        tvIntegral.setText("您当前拥有"+integral+"积分，10积分抽取一次");

        final List<Prize> prizes = new ArrayList<Prize>();
        for (int x = 0; x < 9; x++) {
            Prize lottery = new Prize();
            lottery.setId(x + 1);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), arrImgs[x]);
            lottery.setIcon(bitmap);
            lottery.setDesc(arrDescs[x]);
            lottery.setDescColor(0xFF266353);

            prizes.add(lottery);
        }
        nl.setBmBg(BitmapFactory.decodeResource(getResources(), R.drawable.icon_lottery_bg));
        nl.setBgColor(0xFFCAF3E5);
        nl.setBmChoose(BitmapFactory.decodeResource(getResources(), R.drawable.icon_lottery_choose));
        nl.setBmPrizeBg(BitmapFactory.decodeResource(getResources(), R.drawable.icon_lottery_prize_bg));
        nl.setPrizes(prizes);
        nl.setOnTransferWinningListener(new LotteryView.OnTransferWinningListener() {

            @Override
            public void onClickStart() {
                //请求接口判断能否开始抽奖
                if (integral >= 10) {
                    count++;
                    int awardNumber = 0;
                    switch (count){
                        case 1:
                            awardNumber = 1;
                            break;
                        case 2:
                            awardNumber = 3;
                            break;
                        case 3:
                            awardNumber = 5;
                            break;
                        case 4:
                            awardNumber = 7;
                            break;
                    }
                    nl.setLottery(awardNumber);
                    nl.start();
                }else {
                    Toast.makeText(LotteryActivity.this,"当前积分不足",Toast.LENGTH_SHORT).show();
                    nl.setStartFlags(false);
                }
            }

            @Override
            public void onWinning(int position) {
                integral -= 10;
                tvIntegral.setText("您当前拥有"+integral+"积分，10积分抽取一次");
                Toast.makeText(getApplicationContext(), "恭喜获得："+prizes.get(position).getDesc(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
