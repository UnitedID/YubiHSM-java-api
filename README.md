This is a java library (API) for the YubiHSM, http://yubico.com/yubihsm

Requirements:
- RXTX, a native library providing serial and parallel communication for the Java Development Toolkit (JDK),
  http://rxtx.qbang.org/wiki/index.php/Main_Page

## How to build

    $ mvn package

to skip tests (tests require a YubiHSM configured in 'debug' mode):

    $ mvn -Dmaven.test.skip=true package

## How to configure HSM in debug mode
The HSM is a serial device, I recommend using screen to connect with the device (ie screen /dev/ttyACM0).

Prepare the HSM as follows:

    zap
    yes
    hsm ffffffff
    <enter>
    <enter>
    <enter>
    yes
    exit
    yes

## Usage

Build the javadoc to get the API documentation.

    $ mvn javadoc:javadoc


## Code examples

### Example 1 - Generate a HMACSHA1

    int keyHandle = 12337; // The key to use in the YubiHSM (0x3031)
    String mySecret = "qwerty";

    // Instance of YubiHSM that opens the device /dev/ttyACM0
    YubiHSM hsm = new YubiHSM("/dev/ttyACM0", timeout);

    // Generate HmacSHA1 for mySecret
    String sha1 = hsm.generateHMACSHA1(mySecret, keyHandle, true, false).get("hash");


### Example 2 - OATH-HOTP generate OATH-HOTP AEAD and OTP validation

    // Nonce should stored for later use when validating an OTP
    String randomNonce = "a1a2a3a4a5a6";

    // This is the token seed which should be treated as a secret (don't store this)
    String tokenSeed = "3132333435363738393031323334353637383930";

    // A one-time password from the token with seed above
    String otp = "026920";

    // Current one-time password counter, this counter should be stored and updated
    // for each time the OTP have been validated
    int counter = 30;

    // How many iterations we should look ahead to find a matching OTP
    int lookAhead = 10;

    // This is the key handle with permission to generate AEAD
    int keyHandle = 8192;

    // Instance of YubiHSM that opens the device /dev/ttyACM0
    YubiHSM hsm = new YubiHSM();

    // Generate OATH-HOTP AEAD from the token seed, the AEAD can be safely stored in an database along with the nonce
    // This step is only performed once to recieve an AEAD which can be re-used for OTP validation.
    String aead = hsm.generateOathHotpAEAD(randomNonce, keyHandle, tokenSeed);

    // Validate the one-time password "026920". If the counter result is 0 the validation failed.
    // In this case we expect a value of 31.
    counter = hsm.validateOathHOTP(hsm, keyHandle, randomNonce, aead, counter, otp, lookAhead);
