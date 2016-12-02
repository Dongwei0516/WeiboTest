package com.dongwei.weibotest3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dongwei.weibotest3.utils.AccessTokenKeeper;
import com.dongwei.weibotest3.utils.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.openapi.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.view.View.OnClickListener;


/**
 * Created by dongwei on 2016/11/9.
 */

public class MainActivity extends FragmentActivity {


    private Intent mStartIntent;

    private Context mContext;
    private RecyclerView mRecyclerView;
    private StatusList  mStatus;
    private HomeAdapter mAdapter;
    private Oauth2AccessToken mAccessToken;
    private StatusesAPI mStatusesAPI;
    private UsersAPI mUsersAPI;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RelativeLayout mHomeTab, mMessageTab, mDiscoveryTab, mProfile;
    private ImageView mPostTab;
    public ImageLoader imageLoader = ImageLoader.getInstance();
    private List<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
//    private ArrayList<String> imageDatas;


    private long mExitTime;
    private static final int TYPE_ORINGIN_ITEM = 0;
    private static final int TYPE_RETWEET_ITEM = 3;

    private NineImgView.ClickCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainfragment);

        if (AccessTokenKeeper.readAccessToken(this).isSessionValid()) {

        } else {
            mStartIntent = new Intent(this, UnLoginActivity.class);
            startActivity(mStartIntent);
            finish();
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_widget);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.refreshDrawableState();
                    }
                },500);
            }
        });
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        mContext = this;
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        mUsersAPI = new UsersAPI(this,Constants.APP_KEY,mAccessToken);
        mStatusesAPI = new StatusesAPI(this, Constants.APP_KEY, mAccessToken);
        mHomeTab = (RelativeLayout) findViewById(R.id.tv_home);
        mMessageTab = (RelativeLayout) findViewById(R.id.tv_message);
        mDiscoveryTab = (RelativeLayout) findViewById(R.id.tv_discovery);
        mProfile = (RelativeLayout) findViewById(R.id.tv_profile);
        mPostTab = (ImageView) findViewById(R.id.fl_post);

        setUpListener();

        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            long uid= Long.parseLong(mAccessToken.getUid());
            mStatusesAPI.friendsTimeline(0L, 0L, 50, 1, false, 0, false, mListener);
            mUsersAPI.show(uid, mListener);
        } else {
            Toast.makeText(this, "token不存在，请重新授权", Toast.LENGTH_LONG).show();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.weiboRecyclerView);
        mAdapter= new HomeAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this );
        mRecyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper. VERTICAL);
        mRecyclerView.setAdapter(mAdapter);

    }

    class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ORINGIN_ITEM) {
                OriginViewHolder originViewHolder = new OriginViewHolder(LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.item_weibo, parent, false));
                return originViewHolder;
            } else if (viewType == TYPE_RETWEET_ITEM) {
                RetweetViewHolder retweetViewHolder = new RetweetViewHolder(LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.retweet_item, parent, false));
                return retweetViewHolder;
            }

            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final String userId;
            final String userName;
            final String userImg;
            final String userDesc;
            final String retweetUserId;
            final String retweetUserDesc;
            final String retweetUserName;
            final String retweetUserImg;
            final String statusCount;
            final String friendsCount;
            final String followsCount;
            final String retweetStatusCount;
            final String retweetFriendsCount;
            final String retweetFollowsCount;
            String weiboTime;


            userId = mStatus.statusList.get(position).user.id;
            userName = mStatus.statusList.get(position).user.screen_name;
            userDesc = mStatus.statusList.get(position).user.description;
            statusCount = mStatus.statusList.get(position).user.statuses_count+"";
            followsCount = mStatus.statusList.get(position).user.followers_count+"";
            friendsCount = mStatus.statusList.get(position).user.friends_count+"";
            userImg = mStatus.statusList.get(position).user.avatar_hd;

            weiboTime = mStatus.statusList.get(position).created_at;
            final Date date = new Date(weiboTime);
            SimpleDateFormat sdformat = new SimpleDateFormat("MM/dd/yy h:mmaa");
            String UserTime = sdformat.format(date);

            ImageLoader.getInstance().displayImage(mStatus.statusList.get(position).user.avatar_hd, ((OriginViewHolder) holder).profile_img);
            ((OriginViewHolder) holder).profile_name.setText(mStatus.statusList.get(position).user.screen_name);
            ((OriginViewHolder) holder).profile_time.setText(UserTime);
            ((OriginViewHolder) holder).profile_from.setText(mStatus.statusList.get(position).user.location);
            ((OriginViewHolder) holder).profile_content.setText(mStatus.statusList.get(position).text);
            ((OriginViewHolder) holder).reposts_count.setText(mStatus.statusList.get(position).reposts_count + "");
            ((OriginViewHolder) holder).comments_count.setText(mStatus.statusList.get(position).comments_count + "");
            ((OriginViewHolder) holder).attitudes_count.setText(mStatus.statusList.get(position).attitudes_count + "");

            ((OriginViewHolder) holder).profile_img.setTag(mStatus.statusList.get(position).user.avatar_hd);
            ((OriginViewHolder) holder).profile_img.setImageResource(R.drawable.ic_logo);
            if (((OriginViewHolder) holder).profile_img.getTag() != null && ((OriginViewHolder) holder).profile_img.getTag().equals(mStatus.statusList.get(position).user.avatar_hd)) {
                ((OriginViewHolder) holder).profile_img.setImageResource(R.drawable.ic_logo);
            }

            ((OriginViewHolder) holder).profile_img.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, UserActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("userImg", userImg);
                    intent.putExtra("userName", userName);
                    intent.putExtra("userDesc", userDesc);
                    intent.putExtra("statusCount", statusCount);
                    intent.putExtra("followsCount", followsCount);
                    intent.putExtra("friendsCount", friendsCount);
                    startActivity(intent);
                }
            });

            ((OriginViewHolder) holder).profile_name.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, UserActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("userImg", userImg);
                    intent.putExtra("userName", userName);
                    intent.putExtra("userDesc", userDesc);
                    intent.putExtra("statusCount", statusCount);
                    intent.putExtra("followsCount", followsCount);
                    intent.putExtra("friendsCount", friendsCount);
                    startActivity(intent);
                }
            });

            if(holder instanceof RetweetViewHolder) {

                retweetUserId = mStatus.statusList.get(position).retweeted_status.user.id;
                retweetUserName = mStatus.statusList.get(position).retweeted_status.user.screen_name;
                retweetUserDesc = mStatus.statusList.get(position).retweeted_status.user.description;
                retweetUserImg = mStatus.statusList.get(position).retweeted_status.user.avatar_hd;
                retweetStatusCount = mStatus.statusList.get(position).retweeted_status.user.statuses_count+"";
                retweetFriendsCount = mStatus.statusList.get(position).retweeted_status.user.friends_count+"";
                retweetFollowsCount = mStatus.statusList.get(position).retweeted_status.user.followers_count+"";

                ((RetweetViewHolder) holder).retweet_content.setText(mStatus.statusList.get(position).retweeted_status.text);
                ((RetweetViewHolder) holder).oringin_name.setText(mStatus.statusList.get(position).retweeted_status.user.screen_name);
                ImageLoader.getInstance().displayImage(mStatus.statusList.get(position).retweeted_status.user.avatar_hd,((RetweetViewHolder)holder).oringin_logo);

                final ArrayList<String>imageDatas = mStatus.statusList.get(position).retweeted_status.pic_urls;
                NineImgView nineImgView = ((RetweetViewHolder)holder).ret_imageView;
                nineImgView.setMaxChildCount(imageDatas == null ?0 : imageDatas.size());
                nineImgView.setImgs(imageDatas,imageDatas == null ? 0 : imageDatas.size());

                ClickCallback clickCallback = new ClickCallback();
                ((OriginViewHolder) holder).callback = clickCallback;
                ((RetweetViewHolder)holder).ret_imageView.setClickCallback(((OriginViewHolder) holder).callback);


                ((RetweetViewHolder) holder).oringin_name.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, UserActivity.class);
                        intent.putExtra("userId", retweetUserId);
                        intent.putExtra("userImg", retweetUserImg);
                        intent.putExtra("userName", retweetUserName);
                        intent.putExtra("userDec", retweetUserDesc);
                        intent.putExtra("statusCount", retweetStatusCount);
                        intent.putExtra("followsCount", retweetFollowsCount);
                        intent.putExtra("friendsCount", retweetFriendsCount);
                        startActivity(intent);
                    }
                });

                ((RetweetViewHolder) holder).oringin_logo.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, UserActivity.class);
                        intent.putExtra("userId", retweetUserId);
                        intent.putExtra("userImg", retweetUserImg);
                        intent.putExtra("userName", retweetUserName);
                        intent.putExtra("userDec", retweetUserDesc);
                        intent.putExtra("statusCount", retweetStatusCount);
                        intent.putExtra("followsCount", retweetFollowsCount);
                        intent.putExtra("friendsCount", retweetFriendsCount);
                        startActivity(intent);
                    }
                });

            } else if (holder instanceof OriginViewHolder){

                Log.d("1111", String.valueOf(statusCount));
                Log.d("2222", String.valueOf(followsCount));
                Log.d("3333", String.valueOf(friendsCount));

                final ArrayList<String>imageDatas = mStatus.statusList.get(position).pic_urls;
                Log.d("ImageDatas", String.valueOf(imageDatas));
                final NineImgView nineImgView = ((OriginViewHolder)holder).nineImgView;
                nineImgView.setMaxChildCount(imageDatas == null ? 0 : imageDatas.size());
                nineImgView.setImgs(imageDatas,imageDatas == null ? 0 : imageDatas.size());

                ClickCallback clickCallback = new ClickCallback();
                ((OriginViewHolder) holder).callback = clickCallback;
                ((OriginViewHolder)holder).nineImgView.setClickCallback(((OriginViewHolder) holder).callback);

            }
        }

        @Override
        public int getItemCount() {
            if (mStatus != null) {
                return mStatus.statusList.size();
            } else {
                return 0;
            }
        }

        @Override
        public int getItemViewType(int position){
            if (mStatus.statusList.get(position).retweeted_status != null){
                return TYPE_RETWEET_ITEM;
            }else {
                return TYPE_ORINGIN_ITEM;
            }
        }

        class OriginViewHolder extends RecyclerView.ViewHolder {

            public ImageView profile_img;
            public TextView profile_name;
            public TextView profile_time;
            public TextView profile_from;
            public TextView profile_content;
            public TextView reposts_count;
            public TextView comments_count;
            public TextView attitudes_count;
            public NineImgView nineImgView;
            public ClickCallback callback;

            public OriginViewHolder(View view) {
                super(view);

                profile_img = (ImageView) view.findViewById(R.id.head_logo);
                profile_name = (TextView) view.findViewById(R.id.tv_Name);
                profile_time = (TextView) view.findViewById(R.id.tv_Time);
                profile_from = (TextView) view.findViewById(R.id.tv_From);
                profile_content = (TextView) view.findViewById(R.id.tv_content);
                reposts_count = (TextView) view.findViewById(R.id.redirect);
                comments_count = (TextView) view.findViewById(R.id.comment);
                attitudes_count = (TextView) view.findViewById(R.id.attitude);
                nineImgView = (NineImgView) view.findViewById(R.id.nineImgView);

            }
        }

        class RetweetViewHolder extends OriginViewHolder {


            public TextView oringin_name;
            public ImageView oringin_logo;
            public NineImgView ret_imageView;
            public RelativeLayout retweet_weibo_layout;
            public TextView retweet_content;

            public RetweetViewHolder(View view) {
                super(view);
                retweet_weibo_layout = (RelativeLayout) view.findViewById(R.id.retweet_weibo_layout);
                oringin_logo = (ImageView) view.findViewById(R.id.oringin_head_logo);
                oringin_name = (TextView) view.findViewById(R.id.origin_Name);
                ret_imageView = (NineImgView) view.findViewById(R.id.nineImgView);
                retweet_content = (TextView)view.findViewById(R.id.retweet_content);
            }
        }
    }

    class ClickCallback implements NineImgView.ClickCallback{

        @Override
        public void callback(int index, ArrayList<String> imageDatas) {
            Log.d("CCClick", "true");
            Log.d("index", String.valueOf(index));
            Log.d("AAArrayList", String.valueOf(imageDatas));
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            View imageView = inflater.inflate(R.layout.dialog_photo,null);
            final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
            ImageView img = (ImageView)imageView.findViewById(R.id.largeImage);
            ImageLoader.getInstance().displayImage(imageDatas.get(index).replace("thumbnail", "large"),img);
            dialog.setView(imageView);
            dialog.show();

            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
        }
    }

    private RequestListener mListener = new RequestListener() {

        @Override
        public void onWeiboException(WeiboException e) {

            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(MainActivity.this, info.toString(), Toast.LENGTH_LONG).show();
            mSwipeRefreshLayout.setRefreshing(false);

        }

        @Override
        public void onComplete(final String response) {
            if (!TextUtils.isEmpty(response)) {
                User user = User.parse(response);
                if (response.startsWith("{\"statuses\"")) {
                    StatusList statuses = StatusList.parse(response);
//                    mSwipeRefreshLayout.setRefreshing(false);
                    if (statuses != null && statuses.total_number>0 && user !=null){
                        mStatus = statuses;
                        mAdapter.notifyDataSetChanged();
                    }
                }else if (response.startsWith("{\"created_at\"")) {
                    // 调用 Status#parse 解析字符串成微博对象
                    Status status = Status.parse(response);
//                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }
    };


    private void setUpListener(){
        mHomeTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mMessageTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mPostTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                startActivity(intent);
                finish();

            }
        });
        mDiscoveryTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DiscoverActivity.class);
                startActivity(intent);
                finish();

            }
        });
        mProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出微博", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }


}
