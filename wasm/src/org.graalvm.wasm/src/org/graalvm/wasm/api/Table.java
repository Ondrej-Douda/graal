/*
 * Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.graalvm.wasm.api;

import org.graalvm.wasm.exception.WasmExecutionException;
import org.graalvm.wasm.WasmTable;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;

@ExportLibrary(InteropLibrary.class)
public class Table extends Dictionary {
    private final TableDescriptor descriptor;
    private final WasmTable table;

    public Table(TableDescriptor descriptor) {
        this.descriptor = descriptor;
        // TODO: Instantiate the table.
        this.table = null;
        addMembers(new Object[]{
                        "descriptor", this.descriptor,
                        "grow", new Executable(args -> grow((Long) args[0])),
                        "get", new Executable(args -> get((Long) args[0])),
                        "set", new Executable(args -> set((Long) args[0], args[1])),
        });
    }

    @TruffleBoundary
    private static WasmExecutionException rangeError() {
        return new WasmExecutionException(null, "Range error.");
    }

    public long grow(long delta) {
        final long size = table.size();
        if (!table.grow(delta)) {
            throw rangeError();
        }
        return size;
    }

    public Object get(long index) {
        if (index > table.size()) {
            throw rangeError();
        }
        final Object function = table.get((int) index);
        return function;
    }

    private Object set(long index, Object function) {
        if (index > table.size()) {
            throw rangeError();
        }
        table.set((int) index, function);
        return null;
    }

}
