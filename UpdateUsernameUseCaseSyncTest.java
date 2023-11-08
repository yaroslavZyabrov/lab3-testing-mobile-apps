import com.techyourchance.mockitofundamentals.exercise5.UpdateUsernameUseCaseSync;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync.EndpointResult;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync.EndpointResultStatus;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

public class UpdateUsernameUseCaseSyncTest {

    private UpdateUsernameHttpEndpointSync mUpdateUsernameHttpEndpointSync;
    private UsersCache mUsersCache;
    private EventBusPoster mEventBusPoster;
    private UpdateUsernameUseCaseSync SUT;

    @Before
    public void setup() {
        mUpdateUsernameHttpEndpointSync = mock(UpdateUsernameHttpEndpointSync.class);
        mUsersCache = mock(UsersCache.class);
        mEventBusPoster = mock(EventBusPoster.class);
        SUT = new UpdateUsernameUseCaseSync(mUpdateUsernameHttpEndpointSync, mUsersCache, mEventBusPoster);
    }

    @Test
    public void updateUsernameSync_success_shouldReturnSuccess() throws NetworkErrorException {
        when(mUpdateUsernameHttpEndpointSync.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(
                        UpdateUsernameHttpEndpointSync.EndpointResultStatus.SUCCESS, "userId", "newUsername"
                ));

        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync("userId", "newUsername");

        assertEquals(UpdateUsernameUseCaseSync.UseCaseResult.SUCCESS, result);
        verify(mUsersCache).cacheUser(any(User.class));
        verify(mEventBusPoster).postEvent(any(UserDetailsChangedEvent.class));
    }

    @Test
    public void updateUsernameSync_networkError_shouldReturnNetworkError() throws NetworkErrorException {
        when(mUpdateUsernameHttpEndpointSync.updateUsername(any(String.class), any(String.class)))
                .thenThrow(NetworkErrorException.class);

        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync("userId", "newUsername");

        assertEquals(UpdateUsernameUseCaseSync.UseCaseResult.NETWORK_ERROR, result);
        verify(mUsersCache).cacheUser(any(User.class));
        verify(mEventBusPoster).postEvent(any(UserDetailsChangedEvent.class));
    }

    @Test
    public void updateUsernameSync_serverError_shouldReturnFailure() throws NetworkErrorException {
        when(mUpdateUsernameHttpEndpointSync.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(
                        UpdateUsernameHttpEndpointSync.EndpointResultStatus.SERVER_ERROR, "userId", "newUsername"
                ));

        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync("userId", "newUsername");

        assertEquals(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE, result);
        verify(mUsersCache).cacheUser(any(User.class));
        verify(mEventBusPoster).postEvent(any(UserDetailsChangedEvent.class));
    }

    @Test
    public void updateUsernameSync_authError_shouldReturnFailure() throws NetworkErrorException {
        when(mUpdateUsernameHttpEndpointSync.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(
                        UpdateUsernameHttpEndpointSync.EndpointResultStatus.AUTH_ERROR, "userId", "newUsername"
                ));

        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync("userId", "newUsername");

        assertEquals(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE, result);
        verify(mUsersCache).cacheUser(any(User.class));
        verify(mEventBusPoster).postEvent(any(UserDetailsChangedEvent.class));
    }
}
