package com.dongwei.weibotest3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dongwei on 2016/11/17.
 */

public class MyWeiboActivity extends Activity {

    private ImageView mBack;
    private RecyclerView mRecyclerView;
    private MentionAdapter mAdapter;
    private StatusList mStatus;
    private Oauth2AccessToken mAccessToken;
    private com.sina.weibo.sdk.openapi.legacy.StatusesAPI mStatusesAPI;
    private UsersAPI mUsersAPI;
    public ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mention_weibo_layout);

        mBack = (ImageView)findViewById(R.id.mention_back);
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
//            mStatusesAPI.mentions(0, 0, 30, 1, 0, 0, 0, false, mListener);
            mStatusesAPI.userTimeline(0L, 0L, 30, 1, false, 0, false, mListener);
            mUsersAPI.show(uid, mListener);
        } else {
            Toast.makeText(this, "token不存在，请重新授权", Toast.LENGTH_LONG).show();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.mentionRecyclerView);
        mAdapter= new MyWeiboActivity.MentionAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this );
        mRecyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setAdapter(mAdapter);
    }

    class MentionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
           MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(MyWeiboActivity.this)
                    .inflate(R.layout.item_weibo, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
        {
            final String userId;
            final String userName;
            final String userImg;
            final String userDesc;
            final String statusCount;
            final String friendsCount;
            final String followsCount;
            String weiboTime;

            userId = mStatus.statusList.get(position).user.id;
            userName = mStatus.statusList.get(position).user.screen_name;
            userDesc = mStatus.statusList.get(position).user.description;
            statusCount = mStatus.statusList.get(position).user.statuses_count+"";
            followsCount = mStatus.statusList.get(position).user.followers_count+"";
            friendsCount = mStatus.statusList.get(position).user.friends_count+"";
            userImg = mStatus.statusList.get(position).user.avatar_hd;
            weiboTime = mStatus.statusList.get(position).created_at;
            Date date = new Date(weiboTime);
            SimpleDateFormat sdformat = new SimpleDateFormat("MM/dd/yy h:mmaa");
            String UserTime = sdformat.format(date);
            ImageLoader.getInstance().displayImage(mStatus.statusList.get(position).user.avatar_hd,((MyViewHolder)holder).profile_img);
            ((MyViewHolder)holder).profile_name.setText(mStatus.statusList.get(position).user.screen_name);
            ((MyViewHolder)holder).profile_time.setText(UserTime);
            ((MyViewHolder)holder).profile_content.setText(mStatus.statusList.get(position).text);
            ((MyViewHolder) holder).profile_from.setText(mStatus.statusList.get(position).user.location);
            ((MyViewHolder)holder).reposts_count.setText(mStatus.statusList.get(position).reposts_count+"");
            ((MyViewHolder)holder).comments_count.setText(mStatus.statusList.get(position).comments_count+"");
            ((MyViewHolder)holder).attitudes_count.setText(mStatus.statusList.get(position).attitudes_count+"");
//            ImageLoader.getInstance().displayImage(mStatus.statusList.get(position).bmiddle_pic,((MyViewHolder)holder).imageView);

            ((MyViewHolder) holder).profile_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyWeiboActivity.this, UserActivity.class);
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

            ((MyViewHolder) holder).profile_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyWeiboActivity.this, UserActivity.class);
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
            public TextView profile_from;
            public ImageView imageView;


            public MyViewHolder(View view)
            {
                super(view);
                profile_img = (ImageView) view.findViewById(R.id.head_logo);
                profile_name = (TextView) view.findViewById(R.id.tv_Name);
                profile_time = (TextView) view.findViewById(R.id.tv_Time);
                profile_from = (TextView) view.findViewById(R.id.tv_From);
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
            Toast.makeText(MyWeiboActivity.this, info.toString(), Toast.LENGTH_LONG).show();

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
