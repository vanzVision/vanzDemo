package demo.vanz.com.demotest;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.vanz.librarysdk.Basic.OnVanzCallBack;
import com.vanz.librarysdk.Basic.Vanz;
import com.vanz.librarysdk.Basic.VanzInterface;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private Vanz vanz;
    private VanzInterface view;
    private Handler mainHandler;

    private void createHandler() {
        Looper mainLooper = Looper.getMainLooper();
        mainHandler = new Handler(mainLooper) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        vanz.getGruopData(new OnVanzCallBack.OnSpinner() {
                            @Override
                            public void onSuccess(final List<String> list, final int num) {
                                //样本组
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Spinner spinner = (Spinner) findViewById(R.id.spinner);
                                        loadSpinner(spinner, list, num);
                                        mainHandler.sendEmptyMessage(1);
                                    }
                                });
                            }

                            @Override
                            public void onError(int msg) {

                            }
                        });
                        break;
                    case 1:

                        vanz.getRescSerData(new OnVanzCallBack.OnSpinner() {
                            @Override
                            public void onSuccess(final List<String> list, final int first) {
                                Log.i("===>", "handleMessage: "+list);
                                //资源服务器
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Spinner spinner = (Spinner) findViewById(R.id.spinnerS);
                                        loadSpinner(spinner, list, first);
                                    }
                                });
                            }

                            @Override
                            public void onError(int msg) {

                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final TextView desc = (TextView) findViewById(R.id.Desc);


        view = (VanzInterface) findViewById(R.id.camera);
        view.setCallback(vanzCallback);

        vanz = Vanz.getInstance();
        vanz.init(this);
        createHandler();

        //样本组、资源服务器信息选择获取
        vanz.setOnBasicLib(new OnVanzCallBack.OnBasicLib() {
            @Override
            public void onRegistered() {
                mainHandler.sendEmptyMessage(0);
            }

            @Override
            public void onRegistErr(int err) {
                Log.i("===>", "onRegistErr: " + err);
            }
        });

        vanz.setOnPortDevice(new OnVanzCallBack.OnPortDevice() {
            @Override
            public void Initialize() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        desc.setText("初始化成功");
                    }
                });
            }

            @Override
            public void onWeighing(int state) {
                Log.i("===>", "onWeighing: "+state);
                //1 开始变化 2 稳定有东西 3 稳定没东西
                if (state == 2) {
                    view.TaskIdentify(new VanzInterface.Analyer() {
                        @Override
                        public void onSuccess(JSONObject jsonObject, Bitmap bitmap) {
                            Log.i("===>", "onSuccess: " + jsonObject);
                        }

                        @Override
                        public void onFaileMsg(String msg) {
                            Log.i("===>", "onFaileMsg: "+msg);
                        }

                        @Override
                        public void onError(int msg) {

                        }
                    });
                }
            }
        });

    }

    private VanzInterface.VanzCallback vanzCallback = new VanzInterface.VanzCallback() {
        @Override
        public void onViewReady(String deviceName) {
            //摄像头链接准备成功
        }

        @Override
        public void onViewPasue(String deviceName) {
            //切换界面时视频播放暂停
        }

        @Override
        public void onViewStart(String deviceName) {
            //切换到主界面，视频开始播放
        }

        @Override
        public void onGetConfig() {
            //获取视频截取信息配置
        }

        @Override
        public void onViewError(int err) {

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void loadSpinner(Spinner spinner, final List<String> list, final int x) {
        //数据
        //适配器
        ArrayAdapter<String> arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arr_adapter);
        spinner.setSelection(x, true);

        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {//选择item的选择点击监听事件
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                // 将所选mySpinner 的值带入myTextView 中
                switch (arg0.getId()) {
                    case R.id.spinner:
//                        Log.i("===>", "您选择的是：样本组"+ arg2 + "个");//文本说明
                        vanz.setGroupData(list.get(arg2));
                        break;
                    case R.id.spinnerS:
                        vanz.setRescSer(list.get(arg2));
                        break;
                }

            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                Log.i("===>", "Nothing");
            }
        });
    }

    public void onResume() {
        super.onResume();
        view.OpenvcInit();
    }

}
