(function webpackUniversalModuleDefinition(root, factory) {
	if(typeof exports === 'object' && typeof module === 'object')
		module.exports = factory();
	else if(typeof define === 'function' && define.amd)
		define([], factory);
	else if(typeof exports === 'object')
		exports["colorMapper"] = factory();
	else
		root["flamegraph"] = root["flamegraph"] || {}, root["flamegraph"]["colorMapper"] = factory();
})(window, function() {
return /******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, { enumerable: true, get: getter });
/******/ 		}
/******/ 	};
/******/
/******/ 	// define __esModule on exports
/******/ 	__webpack_require__.r = function(exports) {
/******/ 		if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
/******/ 			Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
/******/ 		}
/******/ 		Object.defineProperty(exports, '__esModule', { value: true });
/******/ 	};
/******/
/******/ 	// create a fake namespace object
/******/ 	// mode & 1: value is a module id, require it
/******/ 	// mode & 2: merge all properties of value into the ns
/******/ 	// mode & 4: return value when already ns object
/******/ 	// mode & 8|1: behave like require
/******/ 	__webpack_require__.t = function(value, mode) {
/******/ 		if(mode & 1) value = __webpack_require__(value);
/******/ 		if(mode & 8) return value;
/******/ 		if((mode & 4) && typeof value === 'object' && value && value.__esModule) return value;
/******/ 		var ns = Object.create(null);
/******/ 		__webpack_require__.r(ns);
/******/ 		Object.defineProperty(ns, 'default', { enumerable: true, value: value });
/******/ 		if(mode & 2 && typeof value != 'string') for(var key in value) __webpack_require__.d(ns, key, function(key) { return value[key]; }.bind(null, key));
/******/ 		return ns;
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = 6);
/******/ })
/************************************************************************/
/******/ ({

/***/ 6:
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "allocationColorMapper", function() { return allocationColorMapper; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "offCpuColorMapper", function() { return offCpuColorMapper; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "nodeJsColorMapper", function() { return nodeJsColorMapper; });
function pickHex (color1, color2, weight) {
    const w1 = weight
    const w2 = 1 - w1
    const rgb = [Math.round(color1[0] * w1 + color2[0] * w2),
        Math.round(color1[1] * w1 + color2[1] * w2),
        Math.round(color1[2] * w1 + color2[2] * w2)]
    return rgb
}

function generateHash (name) {
    // Return a vector (0.0->1.0) that is a hash of the input string.
    // The hash is computed to favor early characters over later ones, so
    // that strings with similar starts have similar vectors. Only the first
    // 6 characters are considered.
    const MAX_CHAR = 6

    var hash = 0
    var maxHash = 0
    var weight = 1
    var mod = 10

    if (name) {
        for (var i = 0; i < name.length; i++) {
            if (i > MAX_CHAR) { break }
            hash += weight * (name.charCodeAt(i) % mod)
            maxHash += weight * (mod - 1)
            weight *= 0.70
        }
        if (maxHash > 0) { hash = hash / maxHash }
    }
    return hash
}

function allocationColorMapper (d) {
    if (d.highlight) return 'rgb(230, 0, 230)'

    const self = d.data.value
    const total = d.value
    const color = pickHex([0, 255, 40], [196, 245, 233], self / total)

    return `rgb(${color.join()})`
}

function offCpuColorMapper (d) {
    if (d.highlight) return '#E600E6'

    let name = d.data.n || d.data.name
    let vector = 0
    const nameArr = name.split('`')

    if (nameArr.length > 1) {
        name = nameArr[nameArr.length - 1] // drop module name if present
    }
    name = name.split('(')[0] // drop extra info
    vector = generateHash(name)

    const r = 0 + Math.round(55 * (1 - vector))
    const g = 0 + Math.round(230 * (1 - vector))
    const b = 200 + Math.round(55 * vector)

    return 'rgb(' + r + ',' + g + ',' + b + ')'
}

function nodeJsColorMapper (d, originalColor) {
    let color = originalColor

    const { v8_jit: v8JIT, javascript, optimized } = d.data.extras || {}
    // Non-JS JIT frames (V8 builtins) are greyed out.
    if (v8JIT && !javascript) {
        color = '#dadada'
    }
    // JavaScript frames are colored based on optimization level
    if (javascript) {
        let opt = (optimized || 0) / d.value
        let r = 255
        let g = 0
        let b = 0
        if (opt < 0.4) {
            opt = opt * 2.5
            r = 240 - opt * 200
        } else if (opt < 0.9) {
            opt = (opt - 0.4) * 2
            r = 0
            b = 200 - (200 * opt)
            g = 100 * opt
        } else {
            opt = (opt - 0.9) * 10
            r = 0
            b = 0
            g = 100 + (150 * opt)
        }
        color = `rgb(${r} , ${g}, ${b})`
    }
    return color
}


/***/ })

/******/ });
});