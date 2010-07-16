package com.lyndir.lhunath.snaplog.data.service;

import com.google.common.base.Predicate;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.data.object.user.UserProfile;
import java.util.List;


/**
 * <h2>{@link UserDAO}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 16, 2010</i> </p>
 *
 * @author lhunath
 */
public interface UserDAO {

    void update(User user);

    void update(UserProfile userProfile);

    UserProfile findUserProfile(User user);

    List<User> listUsers();

    List<User> listUsers(Predicate<User> predicate);
}
