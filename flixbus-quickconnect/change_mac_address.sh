#!/bin/bash

# The MIT License (MIT)
#
# Copyright (c) 2014 Christian Koepp <christian.koepp@tum.de>
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the 'Software'), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.

# enter your interfaces here
INTERFACES=$1

# like to see what is going on? Set debug to true:
DEBUG=true

for INTERFACE in ${INTERFACES[*]}; do

  $DEBUG && echo "Deactivating $INTERFACE"
  ifconfig $INTERFACE down

  while : ; do
    NEW_MAC=$(python -c "import random; print(':'.join(['%02X' % random.randrange(0,255) for i in range(0,6)]))")
    $DEBUG && echo "Trying to set $NEW_MAC to $INTERFACE"
    ifconfig $INTERFACE hw ether $NEW_MAC > /dev/null 2>&1
    [[ $? -ne 0 ]] || break
    $DEBUG && echo "$INTERFACE rejected address $NEW_MAC"
  done

   $DEBUG && echo "Reactivating $INTERFACE"
   ifconfig $INTERFACE up

done