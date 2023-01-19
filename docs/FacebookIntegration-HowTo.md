#### Facebook integration
As Bonnie is in development stage, in order to be able to log in with Facebook account, you need a facebook developer account. To make your account a developer account go to : https://developers.facebook.com/

Then you need to create a new application and add the Facebook Login product to it.

After that, in Settings->Basic you should find the {App ID} and the {App secret}.

Now you just need to provide these as environment variables:
`client-id={App ID};`
`client-secret={App secret}`

Now everything is set up. You can log in with your Facebook account.