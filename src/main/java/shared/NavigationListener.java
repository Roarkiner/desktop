package shared;

import org.bson.types.ObjectId;

import enums.NavigationRouteEnum;

public interface NavigationListener {
    void navigateTo(NavigationRouteEnum page, ObjectId objectId);
}