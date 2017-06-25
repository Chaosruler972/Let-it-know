package com.jerusalem_open_house.let_it_know.letitknow;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Vector;

/**
 * Created by Chaosruler on 04-Jun-17.
 */

public class firebase_services
{
    private static final String TAG = "TAG1";
    private static FirebaseAuth mAuth;
    private static DatabaseReference mDatabase;
    public static Vector<User> users;
    public static Vector<Event_source> event_sources;
    public static Vector<manual_event> manual_events;
    private static SQL_DB db;
    public static boolean done_sync_users = false;
    private static Context sync_con;
    private static Intent temp_intent;
    private static String username;
    private static String admin;
    public static boolean login(Context act, String username, String password, final Intent intent)
    {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
        {
            mAuth.signOut();
           // return true; // we are already logged int
        }

        Task<AuthResult> res = mAuth.signInWithEmailAndPassword(username,password);
        res.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                Log.d("test","logged in");
                intent.putExtra("login", task.isSuccessful() + "");
            }
        });
        return res.isSuccessful();

    }

    public static void register(final Context act, final String username, final String password, final Intent intent)
    {
        mAuth = FirebaseAuth.getInstance();
        mDatabase.child("Crds").child(username.substring(0,username.indexOf('@'))).setValue(password).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Task res = mAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mDatabase = FirebaseDatabase.getInstance().getReference();
                                String user = mAuth.getCurrentUser().getEmail().substring(0, mAuth.getCurrentUser().getEmail().indexOf('@')); // trims @google.com
                                DatabaseReference userref = mDatabase.child("Users").child(user);
                                userref.child("auto_approve").setValue("no");
                                userref.child("color").setValue("000000");
                                userref.child("name").setValue(user);
                                Toast.makeText(act, "Successfully created user", Toast.LENGTH_SHORT).show();
                                User creating_user = new User();
                                creating_user.id = user;
                                creating_user.name = user;
                                creating_user.color = "000000";
                                creating_user.a_aprove = false;
                                users.add(creating_user);
                                intent.putExtra("register","true");
                            }
                            else
                            {
                                Toast.makeText(act, "Unsuccessful, please try again", Toast.LENGTH_SHORT).show();
                                intent.putExtra("register","false");
                            }
                        }

                    });
                }
                else
                {
                    Toast.makeText(act, "Unsuccessful, please try again", Toast.LENGTH_SHORT).show();
                    intent.putExtra("register","false");
                }
            }
        });


        return;
    }
    public static FirebaseUser getuser()
    {
        mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser();
    }

    public static void log_out()
    {
        mAuth.signOut();
    }

    public static boolean SyncUsers(final Context con)
    {
        if(done_sync_users)
            return true;
        users = new Vector<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Users").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    User user = new User();
                    if(snapshot.child("name").getValue() != null)
                        user.name = snapshot.child("name").getValue().toString();
                    if(snapshot.child("color").getValue()!=null)
                      user.color = snapshot.child("color").getValue().toString();
                    if(snapshot.child("auto_approve").getValue()!=null)
                        user.set_auto_approve(snapshot.child("auto_approve").getValue().toString());
                    user.id = snapshot.getKey();
                    if(snapshot.child("url").getValue() != null)
                    {
                        user.url = snapshot.child("url").getValue().toString();
                    }
                    int i;
                    for(i=0; i<users.size(); i++)
                    {
                        if(users.elementAt(i).id == user.id)
                        {
                            users.elementAt(i).copy(user);
                            break;
                        }
                    }
                    if(i==users.size())
                        users.add(user);
                }
                done_sync_users = true;
                db = new SQL_DB(con);
                Vector<User> dbusers = db.get_users(con);
                int i;
                for(i=0; i<users.size() ; i++)
                {
                    User user = users.elementAt(i);
                    if(db.check_user_exists(con,user))
                    {
                        for(User local: dbusers)
                        {
                            if((local.id != null && local.id.equals(user.id)) && (local.color!=null && user.color!=null &&!local.color.equals(user.color) || (local.name!=null && user.name!=null &&!local.name.equals(user.name)) || (local.url != null && user.url!=null &&!local.url.equals(user.url)) ))
                            {
                                User into = new User();
                                into.copy(user);
                                into.a_aprove = false;
                                db.remove_user(con,user);
                                db.add_user(con,into);
                            }
                        }
                    }
                    else
                        db.add_user(con,user);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }

        });

        return true;
    }
    public static void get_admin(Context con, final Intent intent)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("admin").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                intent.putExtra("admin",dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public static User getUserbyID(String id)
    {
        User user = null;
        for(User selected: users)
        {
            if(selected.id.equals(id))
                user = selected;
        }
        return user;
    }
    public static void set_admin(Context con, String new_admin)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("admin").setValue(new_admin);
    }
    public static void set_user_name(Context con, User user)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(user.id).child("name").setValue(user.name);
    }
    public static void set_user_url(Context con, String id, String url)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(id).child("url").setValue(url);
    }
    public static void set_user_color(Context con, User user)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(user.id).child("color").setValue(user.color);
    }
    public static void set_user_approve(Context con, User user)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(user.a_aprove)
         mDatabase.child("Users").child(user.id).child("auto_approve").setValue("yes");
        else
         mDatabase.child("Users").child(user.id).child("auto_approve").setValue("no");
    }
    public static void remove_event_id(Context con, String providor, String user_id, final String event_id)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(providor).child(user_id).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot snapshot_sources: dataSnapshot.getChildren())
                {
                    if(snapshot_sources.getValue().equals(event_id))
                        snapshot_sources.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }
    public static void user_operation(final Context con, final User user, final String admin_id, final String operation, final String pw)
    {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Crds").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String user_pw = null;
                String admin_pw = null;
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    if(snapshot.getKey().toString().equals(user.id))
                    {
                        user_pw = snapshot.getValue().toString();
                        Log.d("pw",user_pw);
                    }
                    if(snapshot.getKey().toString().equals(admin_id))
                    {
                        admin_pw = snapshot.getValue().toString();
                    }
                }
                mAuth.signOut();
                final String finalAdmin_pw = admin_pw;
                assert user_pw != null;
                final String finalUser_pw = user_pw;
                mAuth.signInWithEmailAndPassword(user.id+"@gmail.com",user_pw).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            if (operation.equals("Delete"))
                            {
                                final String finalAdmin_pw1 = finalAdmin_pw;
                                mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            mAuth.signOut();
                                            mAuth.signInWithEmailAndPassword(admin_id+"@gmail.com",finalAdmin_pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task)
                                                {
                                                    if(task.isSuccessful())
                                                    {
                                                        mDatabase.child("Crds").child(user.id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                mDatabase.child("Facebook").child(user.id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        mDatabase.child("Google").child(user.id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                mDatabase.child("Users").child(user.id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        Toast.makeText(con, "Sucessfully deleted user", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                });
                                                                            }
                                                                        });
                                                                    }
                                                                });

                                                            }
                                                        });
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(con, "Something is wrong with creds", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            mAuth.signOut();
                                            login(con, admin_id + "@gmail.com", finalAdmin_pw1, null);
                                            Toast.makeText(con, "Failed to delete user", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                            else if (operation.equals("Change password"))
                            {
                                 final String finalAdmin_pw2 = finalAdmin_pw;
                                mAuth.getCurrentUser().updatePassword(pw).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mAuth.signOut();
                                            login(con, admin_id + "@gmail.com", finalAdmin_pw2,null);
                                            mDatabase.child("Crds").child(user.id).setValue(pw);
                                            Toast.makeText(con, "Sucessfully changed password", Toast.LENGTH_SHORT).show();
                                        } else {
                                            mAuth.signOut();
                                            login(con, admin_id + "@gmail.com", finalAdmin_pw,null);
                                            Toast.makeText(con, "failed to change password", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void SyncProvidors(Context con, Intent intent, String username, String admin)
    {

        event_sources = new Vector<>();
        temp_intent = intent;
        SyncUsers(con);
        sync_con = con;
        this.admin = admin;
        this.username = username;
        return;
    }
    public static void Sync_providor(Context con, final String providor_name)
    {
        mDatabase = mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(providor_name).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
             for(DataSnapshot snapshot_user : dataSnapshot.getChildren())
             {
                 User user = null;
                 for(int i=0; i<users.size();i++)
                 {
                     if(users.elementAt(i).id.equals(snapshot_user.getKey()))
                         user = users.elementAt(i);
                 }
                 for(DataSnapshot event_snapshot : snapshot_user.getChildren())
                 {
                     Event_source src = new Event_source(providor_name,event_snapshot.getValue().toString(),user);
                     event_sources.add(src);
                 }
             }
             temp_intent.putExtra(providor_name,"yes");
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }
    public static void Sync_manual(Context con, final Intent intent, final String username, final boolean all)
    {
        mDatabase = mDatabase = FirebaseDatabase.getInstance().getReference();
        manual_events = new Vector<>();
        mDatabase.child("Manual").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot snapshot_user : dataSnapshot.getChildren())
                {
                    if(all || snapshot_user.getKey().equals(username))
                    {
                        User user = null;
                        for (int i = 0; i < users.size(); i++) {
                            if (users.elementAt(i).id.equals(snapshot_user.getKey()))
                                user = users.elementAt(i);
                        }
                        for (DataSnapshot event_snapshot : snapshot_user.getChildren()) {
                            manual_event event = event_snapshot.getValue(manual_event.class);
                            manual_events.add(event);

                        }
                    }
                }
                intent.putExtra("Manual","yes");
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }
    public static void update_manual_event(Context con, final manual_event source, final manual_event dest)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Manual").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot snapshot_user : dataSnapshot.getChildren())
                {
                        User user = null;
                        for (int i = 0; i < users.size(); i++) {
                            if (users.elementAt(i).id.equals(snapshot_user.getKey()))
                                user = users.elementAt(i);
                        }
                        for (DataSnapshot event_snapshot : snapshot_user.getChildren())
                        {
                            if(event_snapshot.getValue(manual_event.class).id.equals(source.id))
                            {
                                if(dest == null)
                                    event_snapshot.getRef().removeValue();
                                else
                                    event_snapshot.getRef().setValue(dest);
                            }

                        }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }
    public static void add_event(final Context con, String providor_name, final String user_id, String id)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = mDatabase.child(providor_name).child(user_id).push();

        ref.setValue(id).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(con, "Succesfully added event source", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(con, "Failed to add event source", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public static void add_manual_event(final Context con, final manual_event event)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String username = getuser().getEmail().substring(0,getuser().getEmail().indexOf('@'));
        final DatabaseReference ref = mDatabase.child("Manual").child(username).push();
        event.id = ref.getKey();
        ref.setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Log.d("manual_2",event.user.id);
                Toast.makeText(con, "added event", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static void update_notification(final Context con, final String body, String title, final Intent intent)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = mDatabase.child("notification");
        ref.child("title").setValue(title).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    ref.child("body").setValue(body).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            intent.putExtra("Done", "true");
                        }
                    });
                }
            }
        });



    }
    public static void set_active_inactive_notification(Context con, final Intent intent, boolean status )
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child("notification");
        ref.child("active").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                intent.putExtra("Done", "true");
            }
        });
    }
    public static void get_notification(final Context con, final Intent intent)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child("notification");
        ref.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child("title").getValue() != null)
                    intent.putExtra("title",dataSnapshot.child("title").getValue().toString());
                else
                    intent.putExtra("title","null");
                if(dataSnapshot.child("body").getValue() != null)
                     intent.putExtra("body",dataSnapshot.child("body").getValue().toString());
                else
                    intent.putExtra("body","null");
                intent.putExtra("active",dataSnapshot.child("active").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
        intent.putExtra("Done", "true");
    }

}

