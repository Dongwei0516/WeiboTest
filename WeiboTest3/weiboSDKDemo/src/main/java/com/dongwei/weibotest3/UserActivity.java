package com.dongwei.weibotest3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dongwei.weibotest3.utils.AccessTokenKeeper;
import com.dongwei.weibotest3.utils.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.openapi.models.User;

/**
 * Created by dongwei on 2016/11/18.
 */

public class UserActivity extends Activity {

    private ImageView mBack;
    private ImageView mUserImg;
    private TextView mUserName;
    private TextView mUserName2;
    private TextView mUserDesc;
    private TextView mStatusCount;
    private TextView mFriendsCount;
    private TextView mFollowsCount;
    private RecyclerView mRecyclerView;
    private StatusList mStatus;
    private Oauth2AccessToken mAccessToken;
    private com.sina.weibo.sdk.openapi.legacy.StatusesAPI mStatusesAPI;
    private UsersAPI mUsersAPI;
    private UserAdapter mAdapter;
    public ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_layout);
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        String userName = intent.getStringExtra("userName");
        String userDesc = intent.getStringExtra("userDec");
        String statesCount = intent.getStringExtra("statusCount");
        String friendsCount = intent.getStringExtra("friendsCount");
        String followsCount = intent.getStringExtra("followsCount");
        String userImg = intent.getStringExtra("userImg");
        Log.d("UUserId", userId);
//        Log.d("status", statesCount);
//        Log.d("friends", friendsCount);
//        Log.d("follows", followsCount);

        mBack = (ImageView)findViewById(R.id.user_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        mUsersAPI = new UsersAPI(this, Constants.APP_KEY,mAccessToken);
        mStatusesAPI = new com.sina.weibo.sdk.openapi.legacy.StatusesAPI(this, Constants.APP_KEY, mAccessToken);

        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            long uid= Long.parseLong(mAccessToken.getUid());
            mStatusesAPI.userTimeline(userName,0L, 0L, 50, 1, false, 0, false, mListener);
//            mStatusesAPI.homeTimeline(0 ,0 ,50 ,1, false, 0, false , mListener);
            Log.d("11111",userName);
            mUsersAPI.show(uid, mListener);
        } else {
            Toast.makeText(this, "token不存在，请重新授权", Toast.LENGTH_LONG).show();
        }

        mUserImg = (ImageView)findViewById(R.id.user_img);
        ImageLoader.getInstance().displayImage(userImg,mUserImg);
        mUserName = (TextView)findViewById(R.id.username);
        mUserName.setText(userName);
        mUserName2 = (TextView)findViewById(R.id.username2);
        mUserName2.setText(userName);
        mUserDesc = (TextView)findViewById(R.id.user_description);
        mUserDesc.setText(userDesc);
        mStatusCount = (TextView)findViewById(R.id.status_count);
        mStatusCount.setText(statesCount);
        mFriendsCount = (TextView)findViewById(R.id.user_friends_count);
        mFriendsCount.setText(friendsCount);
        mFollowsCount = (TextView)findViewById(R.id.user_followers_count);
        mFollowsCount.setText(followsCount);

        mRecyclerView = (RecyclerView) findViewById(R.id.userRecyclerView);
        mAdapter= new UserActivity.UserAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this );
        mRecyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setAdapter(mAdapter);
    }

    class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(UserActivity.this)
                    .inflate(R.layout.item_weibo, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
        {
            ImageLoader.getInstance().displayImage(mStatus.statusList.get(position).user.avatar_hd,((MyViewHolder)holder).profile_img);
            ((MyViewHolder)holder).profile_name.setText(mStatus.statusList.get(position).user.screen_name);
            ((MyViewHolder)holder).profile_time.setText(mStatus.statusList.get(position).created_at);
            ((MyViewHolder)holder).profile_content.setText(mStatus.statusList.get(position).text);
            ((MyViewHolder)holder).reposts_count.setText(mStatus.statusList.get(position).reposts_count+"");
            ((MyViewHolder)holder).comments_count.setText(mStatus.statusList.get(position).comments_count+"");
            ((MyViewHolder)holder).attitudes_count.setText(mStatus.statusList.get(position).attitudes_count+"");
//            ImageLoader.getInstance().displayImage(mStatus.statusList.get(position).bmiddle_pic,((MyViewHolder)holder).imageView);

        }

        @Override
        public int getItemCount()
        {
            if (mStatus != null) {
                return mStatus.statusList.size();
            } else {
                return 0;
            }
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            public ImageView profile_img;
            public TextView profile_name;
            public TextView profile_time;
            public TextView profile_content;
            public TextView reposts_count;
            public TextView comments_count;
            public TextView attitudes_count;
            public ImageView imageView;


            public MyViewHolder(View view)
            {
                super(view);
                profile_img = (ImageView) view.findViewById(R.id.head_logo);
                profile_name = (TextView) view.findViewById(R.id.tv_Name);
                profile_time = (TextView) view.findViewById(R.id.tv_Time);
                profile_content = (TextView) view.findViewById(R.id.tv_content);
                reposts_count = (TextView)view.findViewById(R.id.redirect);
                comments_count = (TextView)view.findViewById(R.id.comment);
                attitudes_count = (TextView)view.findViewById(R.id.attitude);
//                imageView = (ImageView)view.findViewById(R.id.weibo_img);
            }
        }
    }


    private RequestListener mListener = new RequestListener() {

        @Override
        public void onWeiboException(WeiboException e) {

            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(UserActivity.this, info.toString(), Toast.LENGTH_LONG).show();

        }

        @Override
        public void onComplete(final String response) {
            if (!TextUtils.isEmpty(response)) {
                User user = User.parse(response);
                if (response.startsWith("{\"statuses\"")) {
                    StatusList statuses = StatusList.parse(response);
                    if (statuses != null && statuses.total_number>0 && user !=null){
                        mStatus = statuses;
                        mAdapter.notifyDataSetChanged();
                    }
                }else if (response.startsWith("{\"created_at\"")) {
                    Status status = Status.parse(response);
                }
            }
        }
    };
}
